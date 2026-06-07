package com.xkball.roads.block.collidetest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

public class QuadCollection {

    private final List<Quad> quads;

    public QuadCollection(List<Quad> quads) {
        this.quads = List.copyOf(quads);
    }

    public List<Quad> quads() {
        return quads;
    }

    public boolean isEmpty() {
        return quads.isEmpty();
    }

    public static QuadCollection empty() {
        return new QuadCollection(List.of());
    }

    public record Quad(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3) {
        private static final Codec<Vector3f> VEC3_CODEC = Codec.FLOAT.listOf(3, 3).xmap(
                list -> new Vector3f(list.get(0), list.get(1), list.get(2)),
                v -> List.of(v.x, v.y, v.z)
        );

        public static final Codec<Quad> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VEC3_CODEC.fieldOf("v0").forGetter(Quad::v0),
                VEC3_CODEC.fieldOf("v1").forGetter(Quad::v1),
                VEC3_CODEC.fieldOf("v2").forGetter(Quad::v2),
                VEC3_CODEC.fieldOf("v3").forGetter(Quad::v3)
        ).apply(instance, Quad::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Quad> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public Quad decode(RegistryFriendlyByteBuf buf) {
                return new Quad(
                        new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                        new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                        new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                        new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat())
                );
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, Quad quad) {
                writeVec3(buf, quad.v0);
                writeVec3(buf, quad.v1);
                writeVec3(buf, quad.v2);
                writeVec3(buf, quad.v3);
            }

            private void writeVec3(RegistryFriendlyByteBuf buf, Vector3f v) {
                buf.writeFloat(v.x);
                buf.writeFloat(v.y);
                buf.writeFloat(v.z);
            }
        };
    }

    public static final Codec<QuadCollection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Quad.CODEC.listOf().fieldOf("quads").forGetter(QuadCollection::quads)
    ).apply(instance, QuadCollection::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuadCollection> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public QuadCollection decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<Quad> quads = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                quads.add(Quad.STREAM_CODEC.decode(buf));
            }
            return new QuadCollection(quads);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, QuadCollection collection) {
            buf.writeVarInt(collection.quads.size());
            for (Quad quad : collection.quads) {
                Quad.STREAM_CODEC.encode(buf, quad);
            }
        }
    };
}
