package com.xkball.roads.item;

import com.xkball.roads.Roads;
import com.xkball.roads.block.ModBlocks;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Roads.MODID);

    public static final DeferredItem<BlockItem> ROAD_BUILDER = ITEMS.registerSimpleBlockItem("road_builder", ModBlocks.ROAD_BUILDER);

}
