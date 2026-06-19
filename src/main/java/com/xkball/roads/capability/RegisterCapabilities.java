package com.xkball.roads.capability;

import com.xkball.roads.Roads;
import com.xkball.roads.blockentity.BaseRoadsBlockEntity;
import com.xkball.roads.blockentity.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Roads.MODID)
public class RegisterCapabilities {

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        ModBlockEntities.BASE_BLOCK_ENTITIES.getEntries().forEach(holder -> {
            BlockEntityType<? extends BaseRoadsBlockEntity> blockEntityType = (BlockEntityType<? extends BaseRoadsBlockEntity>) holder.get();
            event.registerBlockEntity(Capabilities.Energy.BLOCK,
                    blockEntityType,
                    (be, side) -> be.energyHandler);
            event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                    blockEntityType,
                    (be, side) -> be.fluidStacksResourceHandler);
            event.registerBlockEntity(Capabilities.Item.BLOCK,
                    blockEntityType,
                    (be, side) -> be.itemStackHandler);
        });

    }
}
