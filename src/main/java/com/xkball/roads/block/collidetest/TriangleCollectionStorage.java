package com.xkball.roads.block.collidetest;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.xkball.roads.Roads;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;

public class TriangleCollectionStorage {

    public static void put(Level level, BlockPos pos, Block block) {
        if (level.isClientSide()) {
            return;
        }
        var collection = load(level.getServer(), block);
        if (collection == null) {
            return;
        }
        LevelChunk chunk = level.getChunkAt(pos);
        var attachmentType = ModAttachments.TRIANGLE_COLLECTION.get();
        Map<BlockPos, TriangleCollection> data = chunk.getData(attachmentType);
        Map<BlockPos, TriangleCollection> newData = new HashMap<>(data);
        newData.put(pos.immutable(), collection);
        chunk.setData(attachmentType, newData);
        chunk.markUnsaved();
    }

    public static void remove(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }
        LevelChunk chunk = level.getChunkAt(pos);
        var attachmentType = ModAttachments.TRIANGLE_COLLECTION.get();
        Map<BlockPos, TriangleCollection> data = chunk.getData(attachmentType);
        if (data.isEmpty() || !data.containsKey(pos)) {
            return;
        }
        Map<BlockPos, TriangleCollection> newData = new HashMap<>(data);
        newData.remove(pos);
        chunk.setData(attachmentType, newData);
        chunk.markUnsaved();
    }

    private static TriangleCollection load(MinecraftServer server, Block block) {
        if (server == null) {
            return null;
        }
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
        Identifier resourceId = Identifier.fromNamespaceAndPath(
                blockId.getNamespace(),
                "tri_collections/" + blockId.getPath() + ".json"
        );
        var resource = server.getResourceManager().getResource(resourceId);
        if (resource.isEmpty()) {
            Roads.LOGGER.warn("Missing triangle collection resource {}", resourceId);
            return null;
        }
        return parse(resource.get(), resourceId);
    }

    private static TriangleCollection parse(Resource resource, Identifier resourceId) {
        try (Reader reader = resource.openAsReader()) {
            JsonElement json = StrictJsonParser.parse(reader);
            var result = TriangleCollection.CODEC.parse(JsonOps.INSTANCE, json);
            var collection = result.resultOrPartial(error -> Roads.LOGGER.error("Failed to parse triangle collection {}: {}", resourceId, error));
            return collection.orElse(null);
        } catch (IOException | RuntimeException e) {
            Roads.LOGGER.error("Failed to read triangle collection {}", resourceId, e);
            return null;
        }
    }
}
