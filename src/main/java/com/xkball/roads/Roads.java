package com.xkball.roads;

import com.xkball.roads.block.ModBlocks;
import com.xkball.roads.block.collidetest.ModAttachments;
import com.xkball.roads.block.collidetest.QuadCollection;
import com.xkball.roads.item.ModItems;
import java.util.HashMap;
import java.util.Map;

import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

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

    // FIXME: 独立服务器不可用 - 依赖BreakBlockEvent.
    // FIXME: block移除逻辑当前使用NeoForge事件, 后续可能需要改为监听服务端原生事件.
    @SubscribeEvent
    public void onBlockBreak(BreakBlockEvent event) {
        if (event.getState().getBlock() instanceof com.xkball.roads.block.CollideTestBlock) {
            Level level = (Level) event.getLevel();
            BlockPos pos = event.getPos();
            LevelChunk chunk = level.getChunkAt(pos);
            var attachmentType = ModAttachments.QUAD_COLLECTION.get();
            Map<BlockPos, QuadCollection> data = chunk.getData(attachmentType);
            if (!data.isEmpty()) {
                Map<BlockPos, QuadCollection> newData = new HashMap<>(data);
                newData.remove(pos);
                chunk.setData(attachmentType, newData);
                chunk.markUnsaved();
            }
        }
    }
}
