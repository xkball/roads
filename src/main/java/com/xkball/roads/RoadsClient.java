package com.xkball.roads;

import com.xkball.roads.block.ModBlocks;
import com.xkball.roads.client.renderer.RoadBuilderBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Roads.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Roads.MODID, value = Dist.CLIENT)
public class RoadsClient {
    
    public RoadsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlocks.ROAD_BUILDER_BLOCK_ENTITY.get(), RoadBuilderBlockEntityRenderer::new);
    }
}
