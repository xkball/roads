package com.xkball.roads.block;

import com.xkball.roads.blockentity.ModBlockEntities;
import com.xkball.roads.blockentity.TestBlockEntity;
import com.xkball.roads.multi.Structure;
import com.xkball.roads.multi.Structures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TestBlock extends RoadBaseStructureEntityBlock{

    public TestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.TEST_BLOCK_ENTITY.get(), TestBlockEntity::tick);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
        return new TestBlockEntity(worldPosition, blockState);
    }

    @Override
    public Structure getStructure() {
        return Structures.TEST_STRUCTURE;
    }
}
