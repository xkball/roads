package com.xkball.roads.capability;

import com.xkball.roads.Roads;
import com.xkball.roads.blockentity.ModBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Roads.MODID)
public class RegisterCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ModBlockEntities.GENERATOR_BLOCK_ENTITY.get(),
                (be, side) -> be.energyHandler);
    }
}
