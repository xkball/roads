package com.xkball.roads.multi;

import net.minecraft.world.level.block.Block;

import java.util.*;

/**
 * 抽象一个多方块结构类出来，定义多方块怎么摆，以及内部方块对应能操作什么。
 * 这个结构类会在主方块的te里面存着，以便生成并操纵子方块的te数据
 */
public class Structure {
    private final List<String[]> structure;
    private final Map<Character, BlockInfo> structureInfos;

    private Structure(List<String[]> structure, Map<Character, BlockInfo> structureInfos) {
        this.structure = List.copyOf(structure);
        this.structureInfos = Map.copyOf(structureInfos);
    }

    public List<String[]> getStructure() {
        return structure;
    }

    public Map<Character, BlockInfo> getStructureInfos() {
        return structureInfos;
    }

    public static class StructureBuilder {
        private final List<String[]> structure = new ArrayList<>();
        private final Map<Character, BlockInfo> structureBlockInfos = new HashMap<>();

        public StructureBuilder setStructureInfo(String[]... structureInfo) {
            structure.addAll(Arrays.asList(structureInfo));
            return this;
        }

        /**
         * @param flags Check com.xkball.roads.multi.Structure.BlockInfo
         *
         */
        public StructureBuilder setStructureBlockInfo(char c, Block block, int flags) {
            structureBlockInfos.put(c, new BlockInfo(block, flags));
            return this;
        }

        public Structure build() {
            return new Structure(structure, structureBlockInfos);
        }
    }


    public static class BlockInfo {
        private BlockInfo(Block block, int flags) {
            this.block = block;
            this.flags = flags;
        }

        //TODO 拆成in/out的enum和直接对cap的引用

        public static final int NULL = 0b00000;
        public static final int INPUT = 0b00001;
        public static final int OUTPUT = 0b00010;
        public static final int ITEM = 0b00100;
        public static final int FLUID = 0b01000;
        public static final int ENERGY = 0b10000;

        public static final int ALL = INPUT | OUTPUT | ITEM | FLUID | ENERGY;

        private final Block block;
        private final int flags;

        public Block getBlock() {
            return block;
        }

        public int getFlags() {
            return flags;
        }
    }

}
