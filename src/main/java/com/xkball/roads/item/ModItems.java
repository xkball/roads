package com.xkball.roads.item;

import com.xkball.roads.Roads;
import com.xkball.roads.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "roads" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Roads.MODID);

    public static final DeferredItem<BlockItem> TEST_BLOCK_ITEM =
            ITEMS.registerItem("test_block", p -> new RoadsStructureItem(ModBlocks.TEST_BLOCK.get(), p));
    public static final DeferredItem<Item> STRUCTURAL_SELECTION_TOOL =
            ITEMS.registerItem("structural_selection_tool", StructuralSelectionTool::new);

    public static final DeferredItem<BlockItem> FAKE_MAIN_BLOCK_ITEM =
            ITEMS.registerItem("fake_main_block", p -> new RoadsStructureItem(ModBlocks.FAKE_MAIN_BLOCK.get(), p));
    public static final DeferredItem<BlockItem> FAKE_STRUCTURE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem("fake_structure_block", ModBlocks.FAKE_STRUCTURE_BLOCK);
    public static final DeferredItem<BlockItem> GENERATOR_BLOCK_ITEM =
            ITEMS.registerItem("generator_block", p -> new RoadsStructureItem(ModBlocks.GENERATOR_BLOCK.get(), p));
}
