package com.xkball.roads.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public abstract class BaseRoadsBlockEntity extends BlockEntity {
    public EnergyHandler energyHandler = new SimpleEnergyHandler(getMaxEnergy()) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public FluidStacksResourceHandler fluidStacksResourceHandler = new FluidStacksResourceHandler(getFluidSize(), getMaxFluid());
    public ItemStacksResourceHandler itemStackHandler = new ItemStacksResourceHandler(getItemSize());


    public BaseRoadsBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    abstract int getMaxEnergy();

    abstract int getMaxFluid();

    abstract int getFluidSize();

    abstract int getItemSize();

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
        fluidStacksResourceHandler.deserialize(input);
        itemStackHandler.deserialize(input);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        ((SimpleEnergyHandler) energyHandler).serialize(output);
        fluidStacksResourceHandler.serialize(output);
        itemStackHandler.serialize(output);
    }

}
