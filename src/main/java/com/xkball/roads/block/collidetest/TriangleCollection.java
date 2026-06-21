package com.xkball.roads.block.collidetest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

public class TriangleCollection {

    private final List<Triangle> triangles;

    public TriangleCollection(List<Triangle> triangles) {
        this.triangles = List.copyOf(triangles);
    }

    public List<Triangle> triangles() {
        return triangles;
    }

    public boolean isEmpty() {
        return triangles.isEmpty();
    }

    public static TriangleCollection empty() {
        return new TriangleCollection(List.of());
    }

    public record Triangle(Vector3f v0, Vector3f v1, Vector3f v2) {
        private static final Codec<Vector3f> VEC3_CODEC = Codec.FLOAT.listOf(3, 3).xmap(
                list -> new Vector3f(list.get(0), list.get(1), list.get(2)),
                v -> List.of(v.x, v.y, v.z)
        );

        public static final Codec<Triangle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VEC3_CODEC.fieldOf("v0").forGetter(Triangle::v0),
                VEC3_CODEC.fieldOf("v1").forGetter(Triangle::v1),
                VEC3_CODEC.fieldOf("v2").forGetter(Triangle::v2)
        ).apply(instance, Triangle::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Triangle> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public Triangle decode(RegistryFriendlyByteBuf buf) {
                return new Triangle(
                        new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                        new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                        new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat())
                );
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, Triangle triangle) {
                writeVec3(buf, triangle.v0);
                writeVec3(buf, triangle.v1);
                writeVec3(buf, triangle.v2);
            }

            private void writeVec3(RegistryFriendlyByteBuf buf, Vector3f v) {
                buf.writeFloat(v.x);
                buf.writeFloat(v.y);
                buf.writeFloat(v.z);
            }
        };
    }

    public static final Codec<TriangleCollection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Triangle.CODEC.listOf().fieldOf("triangles").forGetter(TriangleCollection::triangles)
    ).apply(instance, TriangleCollection::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TriangleCollection> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TriangleCollection decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<Triangle> triangles = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                triangles.add(Triangle.STREAM_CODEC.decode(buf));
            }
            return new TriangleCollection(triangles);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, TriangleCollection collection) {
            buf.writeVarInt(collection.triangles.size());
            for (Triangle triangle : collection.triangles) {
                Triangle.STREAM_CODEC.encode(buf, triangle);
            }
        }
    };
}
