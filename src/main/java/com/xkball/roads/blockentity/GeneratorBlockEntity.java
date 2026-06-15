package com.xkball.roads.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity {
    private static final int MAX_ENERGY = 100_000;
    private static final int GENERATE_RATE = 20;
    private BlockCapabilityCache<EnergyHandler, @Nullable Direction> capCache;

    public EnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

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
    @NotNull
    public CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return super.getUpdateTag(registries);
    }

    @Override
    public void handleUpdateTag(@NonNull ValueInput input) {
        super.handleUpdateTag(input);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        ((SimpleEnergyHandler) energyHandler).deserialize(input);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        ((SimpleEnergyHandler) energyHandler).serialize(output);
    }
}
