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
    private static final int MAX_RESOLVE_ITERATIONS = 4;


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
            Vector3d resolved = resolveMovement(box, new Vector3d(movement.x, movement.y, movement.z), entity.maxUpStep(), triangles);
            return new Vec3(resolved.x, resolved.y, resolved.z);
        } catch (RuntimeException e) {
            Roads.LOGGER.error("Failed to resolve triangle collision", e);
            return movement;
        }
    }

    private static Vector3d resolveMovement(AABB box, Vector3d movement, double maxUpStep, Vector3fc[] triangles) {
        List<Face> faces = buildFaces(triangles);
        Vector3d resolved = resolveMovementWithoutStep(box, movement, faces);
        if (maxUpStep <= EPSILON || movement.y() > EPSILON || horizontalLengthSquared(movement) <= EPSILON) {
            return resolved;
        }
        if (horizontalLengthSquared(resolved) + EPSILON >= horizontalLengthSquared(movement)) {
            return resolved;
        }
        Vector3d stepped = resolveStepMovement(box, movement, maxUpStep, faces);
        if (horizontalLengthSquared(stepped) > horizontalLengthSquared(resolved) + EPSILON) {
            return stepped;
        }
        return resolved;
    }

    private static Vector3d resolveStepMovement(AABB box, Vector3d movement, double maxUpStep, List<Face> faces) {
        Vector3d up = resolveMovementWithoutStep(box, new Vector3d(0.0, maxUpStep, 0.0), faces);
        if (up.y() <= EPSILON) {
            return new Vector3d();
        }
        AABB upBox = move(box, up);
        Vector3d horizontal = new Vector3d(movement.x(), 0.0, movement.z());
        Vector3d movedHorizontal = resolveMovementWithoutStep(upBox, horizontal, faces);
        if (horizontalLengthSquared(movedHorizontal) <= EPSILON) {
            return up;
        }
        AABB horizontalBox = move(upBox, movedHorizontal);
        Vector3d down = resolveMovementWithoutStep(horizontalBox, new Vector3d(0.0, movement.y() - up.y(), 0.0), faces);
        return up.add(movedHorizontal, new Vector3d()).add(down);
    }

    private static Vector3d resolveMovementWithoutStep(AABB box, Vector3d movement, List<Face> faces) {
        Vector3d remaining = new Vector3d(movement);
        Vector3d resolved = new Vector3d();
        for (int i = 0; i < MAX_RESOLVE_ITERATIONS; i++) {
            if (remaining.lengthSquared() <= EPSILON * EPSILON) {
                return resolved;
            }
            Collision collision = findFirstCollision(box, remaining, faces);
            if (collision == null) {
                return resolved.add(remaining);
            }
            double dot = remaining.dot(collision.face().normal());
            if (collision.initialOverlap() && dot < -EPSILON) {
                return resolved.add(collision.face().normal().mul(Math.max(EPSILON, remaining.length()), new Vector3d()));
            }
            if (dot > EPSILON) {
                Vector3d projected = projectToFace(remaining, collision.face().normal());
                if (projected.lengthSquared() <= EPSILON * EPSILON) {
                    return resolved;
                }
                remaining = projected;
                continue;
            }
            Vector3d safe = new Vector3d(remaining).mul(Math.max(0.0, collision.fraction() - EPSILON));
            resolved.add(safe);
            return resolved;
        }
        return resolved;
    }

    private static Collision findFirstCollision(AABB box, Vector3d movement, List<Face> faces) {
        Vector3d min = new Vector3d(box.minX, box.minY, box.minZ);
        Vector3d max = new Vector3d(box.maxX, box.maxY, box.maxZ);
        double movementLength = movement.length();
        Collision best = null;
        for (Face face : faces) {
            if (intersects(min, max, face)) {
                double dot = movement.dot(face.normal());
                if (dot < -EPSILON) {
                    return new Collision(face, 0.0, true);
                }
                continue;
            }
            Vector3dc swept = CollisionUtils.sweptCollisionAABBTriangle(min, max, movement, face.vertices(), EPSILON);
            double sweptLength = swept.length();
            if (sweptLength + EPSILON >= movementLength) {
                continue;
            }
            double fraction = movementLength <= EPSILON ? 0.0 : sweptLength / movementLength;
            if (best == null || fraction < best.fraction()) {
                best = new Collision(face, fraction, false);
            }
        }
        return best;
    }

    private static boolean intersects(Vector3dc min, Vector3dc max, Face face) {
        return CollisionUtils.intersectsAABBTriangle(min, max, face.vertices(), EPSILON);
    }

    private static Vector3d projectToFace(Vector3d movement, Vector3dc normal) {
        double length = movement.length();
        Vector3d projected = movement.sub(normal.mul(movement.dot(normal), new Vector3d()), new Vector3d());
        if (projected.lengthSquared() <= EPSILON * EPSILON) {
            return new Vector3d();
        }
        return projected.normalize().mul(length);
    }

    private static List<Face> buildFaces(Vector3fc[] triangles) {
        List<Face> faces = new ArrayList<>(triangles.length / 3);
        int triangleCount = triangles.length / 3;
        for (int i = 0; i < triangleCount; i++) {
            Vector3fc v0 = triangles[i * 3];
            Vector3fc v1 = triangles[i * 3 + 1];
            Vector3fc v2 = triangles[i * 3 + 2];
            Vector3d edge0 = new Vector3d(v1).sub(v0);
            Vector3d edge1 = new Vector3d(v2).sub(v0);
            Vector3d normal = edge0.cross(edge1, new Vector3d());
            if (normal.lengthSquared() <= EPSILON * EPSILON) {
                continue;
            }
            faces.add(new Face(v0, v1, v2, normal.normalize()));
        }
        return faces;
    }

    private static double horizontalLengthSquared(Vector3dc vector) {
        return vector.x() * vector.x() + vector.z() * vector.z();
    }

    private static AABB move(AABB box, Vector3dc movement) {
        return box.move(movement.x(), movement.y(), movement.z());
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

    private record Face(Vector3fc v0, Vector3fc v1, Vector3fc v2, Vector3d normal) {
        private Vector3fc[] vertices() {
            return new Vector3fc[] {v0, v1, v2};
        }
    }

    private record Collision(Face face, double fraction, boolean initialOverlap) {
    }
    
}
