package com.xkball.roads.multi;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 用于放置结构检查方法等等
public class StructureUtil {
    private StructureUtil() {}
    /**
     *
     * @param structure 结构信息
     * @return 方块信息列表
     */
    public static List<List<List<Block>>> getStructureBlocks(Structure structure) {
        List<String[]> structure1 = structure.getStructure();
        Map<Character, Structure.BlockInfo> structureInfos = structure.getStructureInfos();

        List<List<List<Block>>> blocks = new ArrayList<>();
        for (String[] s : structure1) {
            List<List<Block>> blocks1 = new ArrayList<>();
            for (String string : s) {
                List<Block> blocks2 = new ArrayList<>();
                string.chars().forEach(c ->{
                    char character = (char) c;
                    Structure.BlockInfo blockInfo = structureInfos.get(character);
                    blocks2.add(blockInfo.getBlock());
                });
                blocks1.add(blocks2);
            }
            blocks.add(blocks1);
        }

        return blocks;
    }
}
