package com.xkball.roads.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TestBlockEntity extends BlockEntity {

    public TestBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.TEST_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TestBlockEntity blockEntity) {
        if (level.nextSubTickCount() % 20 == 0) {
            // 主逻辑
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }
}
