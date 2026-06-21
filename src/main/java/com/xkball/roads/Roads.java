package com.xkball.roads;

import com.xkball.roads.block.CollideTestBlock;
import com.xkball.roads.block.ModBlocks;
import com.xkball.roads.block.collidetest.ModAttachments;
import com.xkball.roads.block.collidetest.TriangleCollectionStorage;
import com.xkball.roads.item.ModItems;

import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod(Roads.MODID)
public class Roads {
    public static final String MODID = "roads";
    public static final Logger LOGGER = LogUtils.getLogger();

    
    public Roads(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);


        NeoForge.EVENT_BUS.register(this);
        
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    
    @SubscribeEvent
    public void onBlockBreak(BreakBlockEvent event) {
        if (event.getState().getBlock() instanceof CollideTestBlock && event.getLevel() instanceof Level level) {
            TriangleCollectionStorage.remove(level, event.getPos());
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().getBlock() instanceof CollideTestBlock && event.getLevel() instanceof Level level) {
            TriangleCollectionStorage.put(level, event.getPos(), event.getPlacedBlock().getBlock());
        }
    }
}
