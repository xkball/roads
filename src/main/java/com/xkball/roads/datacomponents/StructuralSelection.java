package com.xkball.roads.datacomponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.StreamCodec;

public record StructuralSelection(GlobalPos globalPos1, GlobalPos globalPos2) {
    public static final Codec<StructuralSelection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("globalPos1").forGetter(StructuralSelection::globalPos1),
            GlobalPos.CODEC.fieldOf("globalPos2").forGetter(StructuralSelection::globalPos2)
    ).apply(instance, StructuralSelection::new));

    public static final StreamCodec<ByteBuf, StructuralSelection> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, StructuralSelection::globalPos1,
            GlobalPos.STREAM_CODEC, StructuralSelection::globalPos2,
            StructuralSelection::new
    );
}
