package com.xkball.roads.multi;

import com.xkball.roads.block.ModBlocks;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;


// TODO:也许可以改成数据驱动？ by:skyinr
public class Structures {
    // TODO:可能需要做成懒加载，否则使用Identifier作为传参时，可能注册表里面会为null
    public static final Structure GENERATOR_STRUCTURE = new Structure.StructureBuilder()
            .setStructureInfo(new String[]{
                    "M"   // Y=3 top: main generator block
            }, new String[]{
                    "S"   // Y=2: structural coil
            }, new String[]{
                    "S"   // Y=1: structural coil
            }, new String[]{
                    "B"   // Y=0 bottom: structural base
            })
            .setStructureBlockInfo('M', ModBlocks.GENERATOR_BLOCK.get(), Structure.BlockInfo.IOMode.OUTPUT, Capabilities.Energy.BLOCK)
            .setStructureBlockInfo('S', ModBlocks.FAKE_STRUCTURE_BLOCK.get())
            .setStructureBlockInfo('B', ModBlocks.FAKE_STRUCTURE_BLOCK.get())
            .build();

    public static final Structure TEST_STRUCTURE = new Structure.StructureBuilder()
            .setStructureInfo(new String[]{
                    "XXX",
                    "XZX",
                    "XXX"
            }, new String[]{
                    "XXX",
                    "XYX",
                    "XXX"
            }, new String[]{
                    "XXX",
                    "XZX",
                    "XXX"
            })
            .setStructureBlockInfo('X', Blocks.AIR)
            .setStructureBlockInfo('Y', ModBlocks.TEST_BLOCK.get(), Structure.BlockInfo.IOMode.BOTH, Capabilities.Item.BLOCK, Capabilities.Fluid.BLOCK, Capabilities.Energy.BLOCK)
            .setStructureBlockInfo('Z', Blocks.REDSTONE_BLOCK)
            .build();
}