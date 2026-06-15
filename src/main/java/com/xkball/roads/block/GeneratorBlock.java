package com.xkball.roads.block;

import com.xkball.roads.blockentity.GeneratorBlockEntity;
import com.xkball.roads.blockentity.ModBlockEntities;
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

public class GeneratorBlock extends RoadBaseStructureEntityBlock {

    public GeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level,
            @NonNull BlockState blockState, @NonNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.GENERATOR_BLOCK_ENTITY.get(),
                GeneratorBlockEntity::tick);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
        return new GeneratorBlockEntity(worldPosition, blockState);
    }

    @Override
    public Structure getStructure() {
        return Structures.GENERATOR_STRUCTURE;
    }
}
