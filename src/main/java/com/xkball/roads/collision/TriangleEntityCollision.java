package com.xkball.roads.collision;

import com.xkball.roads.Roads;
import com.xkball.roads.block.collidetest.ModAttachments;
import com.xkball.roads.block.collidetest.TriangleCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class TriangleEntityCollision {

    private static final double EPSILON = 1.0E-5;


    public static Vec3 collide(Entity entity, Vec3 movement) {
        if (movement.lengthSqr() < EPSILON) {
            return movement;
        }
        try {
            AABB box = entity.getBoundingBox();
            Vector3fc[] triangles = collectTriangles(entity.level(), box.expandTowards(movement).inflate(EPSILON));
            if (triangles.length == 0) {
                return movement;
            }
            Vector3d resolved = resolveMovement(box, new Vector3d(movement.x, movement.y, movement.z), triangles);
            return new Vec3(resolved.x, resolved.y, resolved.z);
        } catch (RuntimeException e) {
            Roads.LOGGER.error("Failed to resolve triangle collision", e);
            return movement;
        }
    }

    private static Vector3d resolveMovement(AABB box, Vector3d movement, Vector3fc[] triangles) {
        return movement;
    }

    private static Vector3fc[] collectTriangles(Level level, AABB box) {
        List<Vector3fc> result = new ArrayList<>();
        AABB chunkSearchBox = box.inflate(2.0);
        int minChunkX = SectionPos.blockToSectionCoord(chunkSearchBox.minX);
        int maxChunkX = SectionPos.blockToSectionCoord(chunkSearchBox.maxX);
        int minChunkZ = SectionPos.blockToSectionCoord(chunkSearchBox.minZ);
        int maxChunkZ = SectionPos.blockToSectionCoord(chunkSearchBox.maxZ);
        var attachmentType = ModAttachments.TRIANGLE_COLLECTION.get();
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                ChunkAccess chunk = level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
                if (chunk instanceof LevelChunk levelChunk) {
                    Map<BlockPos, TriangleCollection> data = levelChunk.getData(attachmentType);
                    appendChunkTriangles(result, box, data);
                }
            }
        }
        return result.toArray(Vector3fc[]::new);
    }

    private static void appendChunkTriangles(List<Vector3fc> result, AABB box, Map<BlockPos, TriangleCollection> data) {
        for (var entry : data.entrySet()) {
            BlockPos pos = entry.getKey();
            appendTriangles(result, box, pos, entry.getValue());
        }
    }

    private static void appendTriangles(List<Vector3fc> result, AABB box, BlockPos pos, TriangleCollection collection) {
        for (TriangleCollection.Triangle triangle : collection.triangles()) {
            Vector3f v0 = toWorld(pos, triangle.v0());
            Vector3f v1 = toWorld(pos, triangle.v1());
            Vector3f v2 = toWorld(pos, triangle.v2());
            if (intersects(box, v0, v1, v2)) {
                result.add(v0);
                result.add(v1);
                result.add(v2);
            }
        }
    }

    private static boolean intersects(AABB box, Vector3fc v0, Vector3fc v1, Vector3fc v2) {
        float minX = Math.min(Math.min(v0.x(), v1.x()), v2.x());
        float minY = Math.min(Math.min(v0.y(), v1.y()), v2.y());
        float minZ = Math.min(Math.min(v0.z(), v1.z()), v2.z());
        float maxX = Math.max(Math.max(v0.x(), v1.x()), v2.x());
        float maxY = Math.max(Math.max(v0.y(), v1.y()), v2.y());
        float maxZ = Math.max(Math.max(v0.z(), v1.z()), v2.z());
        return box.intersects(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    private static Vector3f toWorld(BlockPos pos, Vector3fc vertex) {
        return new Vector3f(pos.getX() + vertex.x(), pos.getY() + vertex.y(), pos.getZ() + vertex.z());
    }
    
}
