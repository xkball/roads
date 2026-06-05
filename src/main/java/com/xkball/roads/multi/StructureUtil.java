package com.xkball.roads.multi;

import com.xkball.roads.Roads;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.*;

// 用于放置结构检查方法等等
public class StructureUtil {
    private StructureUtil() {
    }

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
                string.chars().forEach(c -> {
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

    /**
     * 输入方块坐标，计算如果在当前方块坐标放置结构。结构中主方块的坐标
     *
     * @param blockPos
     * @param structure
     * @return
     */
    public static BlockPos getStructureOffset(BlockPos blockPos, Structure structure) {
        // TODO 未实现的方法
        return blockPos;
    }


    /**
     * 输入一个矩阵内的方块，输出可用于粘贴进某多方块主类里的结构字符串
     *
     * @param blocks 有序的方块列表
     * @return 可用于粘贴进某多方块主类里的结构字符串
     */
    public static String blocksToStructure(List<List<List<Block>>> blocks) {
        StringBuilder mainSB = new StringBuilder();
        StringBuilder structureInfoSB = new StringBuilder();
        Map<Block, Character> blockCharMap = new HashMap<>();
        char defineChar = 'A';

        mainSB.append("new Structure.StructureBuilder()");

        structureInfoSB.append(".setStructureInfo(");
        for (List<List<Block>> blockY : blocks) {
            structureInfoSB.append("new String[]{\n");
            for (List<Block> blockX : blockY) {
                structureInfoSB.append("\"");
                for (Block block : blockX) {
                    if (defineChar == 'Z' + 1) {
                        Roads.LOGGER.error("定义的方块超过了26个，无法继续定义了");
                        break;
                    }
                    if (blockCharMap.containsKey(block)) {
                        structureInfoSB.append(blockCharMap.get(block));
                        continue;
                    }
                    structureInfoSB.append(defineChar);

                    blockCharMap.put(block, defineChar);

                    defineChar++;

                }
                structureInfoSB.append("\",\n");
            }
            structureInfoSB.deleteCharAt(structureInfoSB.length() - 2);
            structureInfoSB.append("},");
        }
        structureInfoSB.deleteCharAt(structureInfoSB.length() - 1);
        structureInfoSB.append(")");
        try {
            blockCharMap.forEach((block, character) -> {
                Optional<Identifier> identifier = BuiltInRegistries.BLOCK.getResourceKey(block).map(ResourceKey::identifier);
                mainSB.append("\n.setStructureBlockInfo('").append(character).append("',")
                        .append("Identifier.parse(\"")
                        .append(identifier.orElseThrow(() -> new RuntimeException("无法找到方块的Identifier: " + block.getName().getString())))
                        .append("\"))");
            });
            mainSB.append("\n");
        } catch (RuntimeException e) {
            Roads.LOGGER.error("无法找到方块的Identifier，无法生成结构字符串: {}", e.getMessage());
        }

        return mainSB
                .append(structureInfoSB)
                .append("\n.build()")
                .toString();
    }

    public static String blocksToStructure(Level level, AABB aabb){
        List<List<List<Block>>> blocks = new ArrayList<>();

        Iterator<BlockPos> iterator = BlockPos.betweenClosed(aabb).iterator();
        for (int i = 0; i < aabb.getZsize(); i++) {
            List<List<Block>> blocksY = new ArrayList<>();
            for (int j = 0; j < aabb.getYsize(); j++) {
                List<Block> blocksX = new ArrayList<>();
                for (int k = 0; k < aabb.getXsize(); k++) {
                    if (iterator.hasNext()){
                        blocksX.add(level.getBlockState(iterator.next()).getBlock());
                    }
                }
                blocksY.add(blocksX);
            }
            blocks.add(blocksY);
        }

        return blocksToStructure(blocks);
    }
}
