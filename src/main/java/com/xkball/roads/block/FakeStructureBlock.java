package com.xkball.roads.block;

import com.xkball.roads.blockentity.FakeStructureBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FakeStructureBlock extends RoadsBaseEntityBlock {

    public FakeStructureBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
        return new FakeStructureBlockEntity(worldPosition, blockState);
    }
}
