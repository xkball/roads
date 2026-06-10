package com.xkball.roads.block;

import com.xkball.roads.blockentity.FakeMainBlockEntity;
import com.xkball.roads.blockentity.ModBlockEntities;
import com.xkball.roads.multi.Structure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FakeMainBlock extends RoadBaseStructureEntityBlock {
    @Nullable
    private Structure structure;

    public FakeMainBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level,
            @NonNull BlockState blockState, @NonNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.FAKE_MAIN_BLOCK_ENTITY.get(),
                FakeMainBlockEntity::tick);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
        return new FakeMainBlockEntity(worldPosition, blockState);
    }

    public void setStructure(@NonNull Structure structure) {
        this.structure = structure;
    }

    @Override
    @Nullable
    public Structure getStructure() {
        return structure;
    }
}
