package com.xkball.roads.block.collidetest;

import com.mojang.serialization.Codec;
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

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Map<BlockPos, TriangleCollection>>> TRIANGLE_COLLECTION =
            ATTACHMENTS.register("triangle_collection", () -> AttachmentType.<Map<BlockPos, TriangleCollection>>builder(() -> new HashMap<>())
                    .serialize(Codec.unboundedMap(BlockPos.CODEC, TriangleCollection.CODEC).fieldOf("data"))
                    .sync(MAP_STREAM_CODEC)
                    .build());
}
