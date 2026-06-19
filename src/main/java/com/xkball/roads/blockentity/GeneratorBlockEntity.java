package com.xkball.roads.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class GeneratorBlockEntity extends BaseRoadsBlockEntity {
    private static final int MAX_ENERGY = 100_000;
    private static final int GENERATE_RATE = 20;
    private BlockCapabilityCache<EnergyHandler, @Nullable Direction> capCache;

    public GeneratorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.GENERATOR_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        if (level instanceof ServerLevel serverLevel) {
            if (blockEntity.capCache == null) {
                blockEntity.capCache = BlockCapabilityCache.create(
                        Capabilities.Energy.BLOCK,
                        serverLevel,
                        pos,
                        null);
            }


            try (var tr = Transaction.openRoot()) {
                EnergyHandler handler = blockEntity.capCache.getCapability();
                if (handler != null) {
                    handler.insert(GENERATE_RATE, tr);
                    tr.commit();
                }

            }
        }
    }


    @Override
    int getMaxEnergy() {
        return MAX_ENERGY;
    }

    @Override
    int getMaxFluid() {
        return 0;
    }

    @Override
    int getFluidSize() {
        return 0;
    }

    @Override
    int getItemSize() {
        return 0;
    }
}
