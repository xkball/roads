package com.xkball.roads.block;

import com.xkball.roads.Roads;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "roads" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Roads.MODID);

    public static final DeferredBlock<Block> TEST_BLOCK = BLOCKS.registerBlock("test_block", p -> new TestBlock(p.mapColor(MapColor.STONE)));
    public static final DeferredBlock<Block> FAKE_MAIN_BLOCK = BLOCKS.registerBlock("fake_main_block", p -> new FakeMainBlock(p.mapColor(MapColor.STONE)));
    public static final DeferredBlock<Block> FAKE_STRUCTURE_BLOCK = BLOCKS.registerBlock("fake_structure_block", p -> new FakeStructureBlock(p.mapColor(MapColor.STONE)));
    public static final DeferredBlock<Block> GENERATOR_BLOCK = BLOCKS.registerBlock("generator_block", p -> new GeneratorBlock(p.mapColor(MapColor.METAL)));
}
