package com.xkball.roads.datacomponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public record StructuralSelection(BlockPos blockPos1, BlockPos blockPos2) {
    public static final Codec<StructuralSelection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("blockPos1").forGetter(StructuralSelection::blockPos1),
            BlockPos.CODEC.fieldOf("blockPos2").forGetter(StructuralSelection::blockPos2)
    ).apply(instance, StructuralSelection::new));

    public static final StreamCodec<ByteBuf, StructuralSelection> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StructuralSelection::blockPos1,
            BlockPos.STREAM_CODEC, StructuralSelection::blockPos2,
            StructuralSelection::new
    );
}
