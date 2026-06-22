package com.xkball.roads.block.collidetest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.roads.Roads;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Roads.MODID);

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<BlockPos, TriangleCollection>> MAP_STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public Map<BlockPos, TriangleCollection> decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    Map<BlockPos, TriangleCollection> map = new HashMap<>(size);
                    for (int i = 0; i < size; i++) {
                        BlockPos pos = BlockPos.STREAM_CODEC.decode(buf);
                        TriangleCollection collection = TriangleCollection.STREAM_CODEC.decode(buf);
                        map.put(pos, collection);
                    }
                    return map;
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, Map<BlockPos, TriangleCollection> map) {
                    buf.writeVarInt(map.size());
                    for (var entry : map.entrySet()) {
                        BlockPos.STREAM_CODEC.encode(buf, entry.getKey());
                        TriangleCollection.STREAM_CODEC.encode(buf, entry.getValue());
                    }
                }
            };

    private static final Codec<Map<BlockPos, TriangleCollection>> MAP_CODEC = Entry.CODEC.listOf().xmap(
            entries -> {
                Map<BlockPos, TriangleCollection> map = new HashMap<>();
                for (Entry entry : entries) {
                    map.put(entry.pos(), entry.collection());
                }
                return map;
            },
            map -> map.entrySet().stream()
                    .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                    .toList()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Map<BlockPos, TriangleCollection>>> TRIANGLE_COLLECTION =
            ATTACHMENTS.register("triangle_collection", () -> AttachmentType.<Map<BlockPos, TriangleCollection>>builder(() -> new HashMap<>())
                    .serialize(MAP_CODEC.fieldOf("data"))
                    .sync(MAP_STREAM_CODEC)
                    .build());

    private record Entry(BlockPos pos, TriangleCollection collection) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos),
                TriangleCollection.CODEC.fieldOf("collection").forGetter(Entry::collection)
        ).apply(instance, Entry::new));
    }
}
