package com.xkball.roads.multi;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * 抽象一个多方块结构类出来，定义多方块怎么摆，以及内部方块对应能操作什么。
 * 这个结构类会在主方块的te里面存着，以便生成并操纵子方块的te数据
 */
public class Structure {
    private final List<String[]> structure;
    private final Map<Character, @NonNull BlockInfo> structureInfos;

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

        public StructureBuilder setStructureBlockInfo(char c, Block block, BlockInfo.IOMode ioMode, BlockCapability<?, ?>... capabilities) {
            structureBlockInfos.put(c, new BlockInfo(block, ioMode, capabilities));
            return this;
        }

        public StructureBuilder setStructureBlockInfo(char c, Block block) {
            return setStructureBlockInfo(c, block, BlockInfo.IOMode.NONE);
        }

        public StructureBuilder setStructureBlockInfo(char c, Identifier block, BlockInfo.IOMode ioMode, BlockCapability<?, ?>... capabilities) {
            if(BuiltInRegistries.BLOCK.containsKey(block)) {
                return setStructureBlockInfo(c, BuiltInRegistries.BLOCK.getValue(block), ioMode, capabilities);
            }
            throw new IllegalArgumentException("Unknown block id " + block);
        }

        public StructureBuilder setStructureBlockInfo(char c, Identifier block) {
            return setStructureBlockInfo(c, block, BlockInfo.IOMode.NONE);
        }

        public Structure build() {
            return new Structure(structure, structureBlockInfos);
        }
    }


    public static class BlockInfo {

        public enum IOMode {
            NONE,
            INPUT,
            OUTPUT,
            BOTH
        }

        private BlockInfo(Block block, IOMode ioMode, BlockCapability<?, ?>... capabilities) {
            this.block = block;
            this.ioMode = ioMode;
            this.capabilities = Set.of(capabilities);
        }

        private final Block block;
        private final IOMode ioMode;
        private final Set<BlockCapability<?, ?>> capabilities;

        public Block getBlock() {
            return block;
        }

        public IOMode getIOMode() {
            return ioMode;
        }

        public Set<BlockCapability<?, ?>> getCapabilities() {
            return capabilities;
        }

        public boolean hasCapability() {
            return !capabilities.isEmpty();
        }
    }

}
