package com.xkball.roads.item;

import com.xkball.roads.block.RoadBaseStructureEntityBlock;
import com.xkball.roads.multi.StructureUtil;
import net.minecraft.core.BlockPos;
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
        if (getBlock() instanceof RoadBaseStructureEntityBlock block) {
            // 这里放置结构
            // 先检查结构能不能放
            // 如果能放就放
            if (!canPlaceStructure()) return InteractionResult.FAIL;

            for (List<List<Block>> blocks : StructureUtil.getStructureBlocks(block.getStructure())) {
                for (List<Block> block1 : blocks) {
                    for (Block block2 : block1) {
                        BlockState blockState = block2.defaultBlockState();
                        if (block2.isEmpty(blockState)) break;

                        // TODO:放置逻辑 by:skyinr
//                        level.setBlock(clickedPos,block2.defaultBlockState(),3);
                    }
                }
            }
        }
        return super.useOn(context);
    }

    private boolean canPlaceStructure() {
        // 检查结构能不能放

        return true;
    }
}
