package com.xkball.roads.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.xkball.roads.Roads;
import com.xkball.roads.block.collidetest.ModAttachments;
import com.xkball.roads.block.collidetest.QuadCollection;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Math;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class CollideTestBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier MODEL_LOCATION = Identifier.fromNamespaceAndPath(Roads.MODID, "models/block/collide_test");
    private static QuadCollection cachedQuads = null;

    private static final Set<String> FACE_NAMES = Set.of("north", "south", "east", "west", "up", "down");

    public CollideTestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide() && !state.is(oldState.getBlock())) {
            addQuadToChunk(level, pos);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (!movedByPiston) {
            removeQuadFromChunk(level, pos);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    // FIXME: 独立服务器不可用 - level.getServer()在客户端逻辑服务端返回null.
    // FIXME: 后续应使用更可靠的资源加载方式 (如直接读取jar内文件或预计算的QuadCollection常量)
    private static void addQuadToChunk(Level level, BlockPos pos) {
        QuadCollection quads = loadModelQuads(level);
        if (quads == null || quads.isEmpty()) {
            LOGGER.warn("Failed to load model quads for collide_test block at {}", pos);
            return;
        }
        LevelChunk chunk = level.getChunkAt(pos);
        var attachmentType = ModAttachments.QUAD_COLLECTION.get();
        Map<BlockPos, QuadCollection> data = new HashMap<>(chunk.getData(attachmentType));
        data.put(pos.immutable(), quads);
        chunk.setData(attachmentType, data);
        chunk.markUnsaved();
    }

    // FIXME: 独立服务器不可用 - 同上
    private static void removeQuadFromChunk(Level level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        var attachmentType = ModAttachments.QUAD_COLLECTION.get();
        Map<BlockPos, QuadCollection> data = chunk.getData(attachmentType);
        if (data.isEmpty()) {
            return;
        }
        Map<BlockPos, QuadCollection> newData = new HashMap<>(data);
        newData.remove(pos);
        chunk.setData(attachmentType, newData);
        chunk.markUnsaved();
    }

    // FIXME: 独立服务器不可用 - MinecraftServer.getResourceManager()在独立服务器上可用,
    // FIXME: 但模型资源路径可能需要适配数据包环境.
    // FIXME: 简化版JSON模型解析器, 不支持parent继承和face culling, 仅处理elements数组.
    private static QuadCollection loadModelQuads(Level level) {
        if (cachedQuads != null) {
            return cachedQuads;
        }
        try {
            MinecraftServer server = level.getServer();
            if (server == null) {
                LOGGER.warn("Cannot load model quads: server is null");
                return null;
            }
            var resourceManager = server.getResourceManager();
            var optResource = resourceManager.getResource(MODEL_LOCATION);
            if (optResource.isEmpty()) {
                LOGGER.warn("Model resource not found: {}", MODEL_LOCATION);
                return null;
            }
            Resource resource = optResource.get();
            JsonObject json;
            try (var reader = new InputStreamReader(resource.open())) {
                json = JsonParser.parseReader(reader).getAsJsonObject();
            }
            List<QuadCollection.Quad> allQuads = new ArrayList<>();
            JsonArray elements = json.getAsJsonArray("elements");
            if (elements == null) {
                LOGGER.warn("No elements found in model: {}", MODEL_LOCATION);
                return QuadCollection.empty();
            }
            for (JsonElement element : elements) {
                allQuads.addAll(parseElement(element.getAsJsonObject()));
            }
            cachedQuads = new QuadCollection(allQuads);
            LOGGER.info("Loaded {} quads from model {}", allQuads.size(), MODEL_LOCATION);
            return cachedQuads;
        } catch (Exception e) {
            LOGGER.error("Failed to load model quads from {}", MODEL_LOCATION, e);
            return null;
        }
    }

    private static List<QuadCollection.Quad> parseElement(JsonObject element) {
        JsonArray fromArr = element.getAsJsonArray("from");
        JsonArray toArr = element.getAsJsonArray("to");
        float x1 = fromArr.get(0).getAsFloat() / 16f;
        float y1 = fromArr.get(1).getAsFloat() / 16f;
        float z1 = fromArr.get(2).getAsFloat() / 16f;
        float x2 = toArr.get(0).getAsFloat() / 16f;
        float y2 = toArr.get(1).getAsFloat() / 16f;
        float z2 = toArr.get(2).getAsFloat() / 16f;

        JsonObject faces = element.getAsJsonObject("faces");
        if (faces == null) {
            return List.of();
        }

        List<QuadCollection.Quad> quads = new ArrayList<>();
        for (String faceName : faces.keySet()) {
            if (!FACE_NAMES.contains(faceName)) {
                continue;
            }
            QuadCollection.Quad quad = createFaceQuad(x1, y1, z1, x2, y2, z2, faceName);
            quads.add(quad);
        }

        if (element.has("rotation")) {
            JsonObject rotation = element.getAsJsonObject("rotation");
            float angle = rotation.get("angle").getAsFloat();
            String axis = rotation.get("axis").getAsString();
            JsonArray originArr = rotation.getAsJsonArray("origin");
            float ox = originArr.get(0).getAsFloat() / 16f;
            float oy = originArr.get(1).getAsFloat() / 16f;
            float oz = originArr.get(2).getAsFloat() / 16f;
            quads = applyRotation(quads, angle, axis, ox, oy, oz);
        }

        return quads;
    }

    private static QuadCollection.Quad createFaceQuad(float x1, float y1, float z1, float x2, float y2, float z2, String face) {
        return switch (face) {
            case "north" -> new QuadCollection.Quad(
                    new Vector3f(x1, y2, z1), new Vector3f(x2, y2, z1),
                    new Vector3f(x2, y1, z1), new Vector3f(x1, y1, z1));
            case "south" -> new QuadCollection.Quad(
                    new Vector3f(x2, y2, z2), new Vector3f(x1, y2, z2),
                    new Vector3f(x1, y1, z2), new Vector3f(x2, y1, z2));
            case "east" -> new QuadCollection.Quad(
                    new Vector3f(x2, y2, z2), new Vector3f(x2, y2, z1),
                    new Vector3f(x2, y1, z1), new Vector3f(x2, y1, z2));
            case "west" -> new QuadCollection.Quad(
                    new Vector3f(x1, y2, z1), new Vector3f(x1, y2, z2),
                    new Vector3f(x1, y1, z2), new Vector3f(x1, y1, z1));
            case "up" -> new QuadCollection.Quad(
                    new Vector3f(x1, y2, z2), new Vector3f(x2, y2, z2),
                    new Vector3f(x2, y2, z1), new Vector3f(x1, y2, z1));
            case "down" -> new QuadCollection.Quad(
                    new Vector3f(x1, y1, z1), new Vector3f(x2, y1, z1),
                    new Vector3f(x2, y1, z2), new Vector3f(x1, y1, z2));
            default -> throw new IllegalArgumentException("Unknown face: " + face);
        };
    }

    private static List<QuadCollection.Quad> applyRotation(List<QuadCollection.Quad> quads, float angleDeg, String axis, float ox, float oy, float oz) {
        float angleRad = Math.toRadians(angleDeg);
        List<QuadCollection.Quad> rotated = new ArrayList<>();
        for (QuadCollection.Quad quad : quads) {
            rotated.add(new QuadCollection.Quad(
                    rotateVertex(quad.v0(), angleRad, axis, ox, oy, oz),
                    rotateVertex(quad.v1(), angleRad, axis, ox, oy, oz),
                    rotateVertex(quad.v2(), angleRad, axis, ox, oy, oz),
                    rotateVertex(quad.v3(), angleRad, axis, ox, oy, oz)));
        }
        return rotated;
    }

    private static Vector3f rotateVertex(Vector3f v, float angleRad, String axis, float ox, float oy, float oz) {
        float rx = v.x - ox;
        float ry = v.y - oy;
        float rz = v.z - oz;
        float cos = Math.cos(angleRad);
        float sin = Math.sin(angleRad);
        float nx, ny, nz;
        switch (axis) {
            case "x" -> {
                nx = rx;
                ny = ry * cos - rz * sin;
                nz = ry * sin + rz * cos;
            }
            case "y" -> {
                nx = rx * cos + rz * sin;
                ny = ry;
                nz = -rx * sin + rz * cos;
            }
            case "z" -> {
                nx = rx * cos - ry * sin;
                ny = rx * sin + ry * cos;
                nz = rz;
            }
            default -> throw new IllegalArgumentException("Unknown axis: " + axis);
        }
        return new Vector3f(nx + ox, ny + oy, nz + oz);
    }
}
