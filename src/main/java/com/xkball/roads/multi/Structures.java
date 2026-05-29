package com.xkball.roads.multi;

import com.xkball.roads.block.ModBlocks;
import net.minecraft.world.level.block.Blocks;

import static com.xkball.roads.multi.Structure.BlockInfo.*;

public class Structures {
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
                    "XYX",
                    "XXX"
            })
            .setStructureBlockInfo('X', Blocks.AIR, NULL)
            .setStructureBlockInfo('Y', ModBlocks.EXAMPLE_BLOCK.get(), ALL)
            .setStructureBlockInfo('Z', Blocks.REDSTONE_BLOCK, NULL)
            .build();
}