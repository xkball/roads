package com.xkball.roads.block;

import com.xkball.roads.Roads;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "roads" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Roads.MODID);

    // Creates a new Block with the id "roads:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", p -> p.mapColor(MapColor.STONE));
    public static final DeferredBlock<Block> TEST_BLOCK = BLOCKS.registerBlock("test_block", p -> new TestBlock(p.mapColor(MapColor.STONE)));
}
