package com.xkball.roads.item;

import com.xkball.roads.Roads;
import com.xkball.roads.block.ModBlocks;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "roads" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Roads.MODID);
    // Creates a new BlockItem with the id "roads:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK);
    public static final DeferredItem<BlockItem> ROAD_BUILDER = ITEMS.registerSimpleBlockItem("road_builder", ModBlocks.ROAD_BUILDER);
    // Creates a new food item with the id "roads:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", p -> p.food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
}
