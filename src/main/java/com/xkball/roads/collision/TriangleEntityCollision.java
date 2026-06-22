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
    private static final double CONTACT_EPSILON = 1.0E-3;
    private static final int MAX_COLLISION_ITERATIONS = 4;

    public static Vec3 collide(Entity entity, Vec3 movement) {
        if (movement.lengthSqr() == 0.0) {
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
        Vector3d result = new Vector3d();
        Vector3d remaining = new Vector3d(movement);
        AABB currentBox = box;
        for (int i = 0; i < MAX_COLLISION_ITERATIONS; i++) {
            if (remaining.lengthSquared() < 1.0E-12) {
                break;
            }
            Hit hit = findSweepHit(currentBox, remaining, triangles);
            if (hit == null) {
                result.add(remaining);
                break;
            }
            Vector3d travel = hit.travel();
            if (travel.lengthSquared() >= remaining.lengthSquared() - 1.0E-12) {
                result.add(remaining);
                break;
            }
            if (travel.lengthSquared() > 1.0E-12) {
                result.add(travel);
                currentBox = currentBox.move(travel.x, travel.y, travel.z);
            }
            Vector3d next = new Vector3d(remaining).sub(travel);
            projectAwayFromNormal(next, hit.normal());
            remaining.set(next);
        }
        return result;
    }

    private static boolean projectAwayFromNormal(Vector3d movement, Vector3dc normal) {
        double normalMotion = movement.dot(normal);
        if (normalMotion >= -1.0E-7) {
            return false;
        }
        movement.sub(new Vector3d(normal).mul(normalMotion));
        return true;
    }

    private static Hit findSweepHit(AABB box, Vector3dc movement, Vector3fc[] triangles) {
        Hit bestHit = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        int triangleCount = triangles.length / 3;
        for (int i = 0; i < triangleCount; i++) {
            Vector3fc v0 = triangles[i * 3];
            Vector3fc v1 = triangles[i * 3 + 1];
            Vector3fc v2 = triangles[i * 3 + 2];
            Vector3d normal = triangleNormal(v0, v1, v2);
            if (normal.lengthSquared() <= 1.0E-12) {
                continue;
            }
            Vector3dc swept = CollisionUtils.sweptCollisionAABBTriangle(
                    new Vector3d(box.minX, box.minY, box.minZ),
                    new Vector3d(box.maxX, box.maxY, box.maxZ),
                    new Vector3d(movement),
                    new Vector3fc[] {
                            v0,
                            v1,
                            v2
                    },
                    EPSILON
            );
            Vector3d travel = new Vector3d(swept);
            double remainingDistance = movement.lengthSquared();
            double travelDistance = travel.lengthSquared();
            if (travelDistance >= remainingDistance - 1.0E-12) {
                continue;
            }
            normal = orientNormalToBox(box.move(travel.x, travel.y, travel.z), v0, normal);
            if (travelDistance <= 1.0E-12 && -normal.dot(movement) <= 1.0E-7) {
                continue;
            }
            if (travelDistance <= 1.0E-12 && isLeavingContact(box, movement, v0, v1, v2)) {
                continue;
            }
            if (travelDistance < bestDistance) {
                bestDistance = travelDistance;
                bestHit = new Hit(travel, normal);
            }
        }
        return bestHit;
    }

    private static Vector3d orientNormalToBox(AABB box, Vector3fc v0, Vector3d normal) {
        Vector3d center = new Vector3d(
                (box.minX + box.maxX) * 0.5,
                (box.minY + box.maxY) * 0.5,
                (box.minZ + box.maxZ) * 0.5
        );
        Vector3d toBox = center.sub(v0.x(), v0.y(), v0.z());
        if (normal.dot(toBox) < 0.0) {
            normal.negate();
        }
        return normal;
    }

    private static Vector3d triangleNormal(Vector3fc v0, Vector3fc v1, Vector3fc v2) {
        Vector3d edge0 = new Vector3d(v1.x() - v0.x(), v1.y() - v0.y(), v1.z() - v0.z());
        Vector3d edge1 = new Vector3d(v2.x() - v0.x(), v2.y() - v0.y(), v2.z() - v0.z());
        Vector3d normal = edge0.cross(edge1);
        if (normal.lengthSquared() <= 1.0E-12) {
            return normal;
        }
        return normal.normalize();
    }

    private static boolean isLeavingContact(AABB box, Vector3dc movement, Vector3fc v0, Vector3fc v1, Vector3fc v2) {
        Vector3d step = new Vector3d(movement);
        if (step.lengthSquared() <= 1.0E-12) {
            return false;
        }
        step.normalize().mul(CONTACT_EPSILON);
        return !intersectsPrecise(box.move(step.x, step.y, step.z), v0, v1, v2);
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

    private static boolean intersectsPrecise(AABB box, Vector3fc v0, Vector3fc v1, Vector3fc v2) {
        return CollisionUtils.intersectsAABBTriangle(
                new Vector3d(box.minX, box.minY, box.minZ),
                new Vector3d(box.maxX, box.maxY, box.maxZ),
                new Vector3fc[] {
                        v0,
                        v1,
                        v2
                },
                EPSILON
        );
    }

    private static Vector3f toWorld(BlockPos pos, Vector3fc vertex) {
        return new Vector3f(pos.getX() + vertex.x(), pos.getY() + vertex.y(), pos.getZ() + vertex.z());
    }

    private record Hit(Vector3d travel, Vector3d normal) {
    }
}
