package com.xkball.roads.item;

import com.xkball.roads.block.RoadBaseStructureEntityBlock;
import com.xkball.roads.multi.Structure;
import com.xkball.roads.multi.StructureUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class RoadsStructureItem extends BlockItem {

    public RoadsStructureItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @NonNull
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();

        if (getBlock() instanceof RoadBaseStructureEntityBlock block) {
            // 这里放置结构
            // 先检查结构能不能放
            // 如果能放就放
            Structure structure = block.getStructure();
            List<List<List<Block>>> structureBlocks = StructureUtil.getStructureBlocks(structure);
            BlockPos blockPos = StructureUtil.getStructureOffset(clickedPos, structure, block, clickedFace);

            if (!canPlaceStructure(structureBlocks, blockPos, level)) return InteractionResult.FAIL;

            for (int y = 0; y < structureBlocks.size(); y++) {
                List<List<Block>> blockY = structureBlocks.get(y);
                for (int x = 0; x < blockY.size(); x++) {
                    List<Block> blockX = blockY.get(x);
                    for (int z = 0; z < blockX.size(); z++) {
                        Block blockZ = blockX.get(z);
                        BlockState blockState = blockZ.defaultBlockState();

                        if (blockZ.isEmpty(blockState)) continue;

                        level.setBlockAndUpdate(blockPos.offset(x, y, z), blockState);

                    }
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private boolean canPlaceStructure(List<List<List<Block>>> structureBlocks, BlockPos blockPos, Level level) {
        boolean result = true;
        // 检查结构能不能放
        for (int y = 0; y < structureBlocks.size(); y++) {
            List<List<Block>> blockY = structureBlocks.get(y);
            for (int x = 0; x < blockY.size(); x++) {
                List<Block> blockX = blockY.get(x);
                for (int z = 0; z < blockX.size(); z++) {
                    Block blockZ = blockX.get(z);

                    if (blockZ.defaultBlockState().isEmpty()) continue;

                    result &= level.getBlockState(blockPos.offset(x, y, z)).canBeReplaced();
                }
            }
        }

        return result;
    }
}
