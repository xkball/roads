package com.xkball.roads.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
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

    public ResourceHandler<FluidResource> fluidHandler = new FluidStacksResourceHandler(getFluidSize(), getMaxFluid());
    public ResourceHandler<ItemResource> itemHandler = new ItemStacksResourceHandler(getItemSize());


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
        ((FluidStacksResourceHandler) fluidHandler).deserialize(input);
        ((ItemStacksResourceHandler) itemHandler).deserialize(input);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        ((SimpleEnergyHandler) energyHandler).serialize(output);
        ((FluidStacksResourceHandler) fluidHandler).serialize(output);
        ((ItemStacksResourceHandler) itemHandler).serialize(output);
    }

}
