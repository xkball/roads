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
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK);
    public static final DeferredItem<BlockItem> COLLIDE_TEST_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("collide_test_block", ModBlocks.COLLIDE_TEST_BLOCK);
    public static final DeferredItem<BlockItem> COLLIDE_TEST_BLOCK_ITEM2 = ITEMS.registerSimpleBlockItem("collide_test_block2", ModBlocks.COLLIDE_TEST_BLOCK2);

    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", p -> p.food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
}
