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

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
            Map<BlockPos, Block> structureBlockToMap = StructureUtil.getStructureBlockToMap(structure);
            BlockPos blockPos = StructureUtil.getStructureOffset(clickedPos, structure, block, clickedFace);

            if (!canPlaceStructure(structureBlockToMap, blockPos, level)) return InteractionResult.FAIL;

            structureBlockToMap.forEach((blockPos1, block1) ->{
                BlockState blockState = block1.defaultBlockState();
                if (blockState.isEmpty()) return;
                level.setBlockAndUpdate(blockPos.offset(blockPos1), blockState);
            });
        }

        return InteractionResult.SUCCESS;
    }

    private boolean canPlaceStructure(Map<BlockPos, Block> structureBlocks, BlockPos blockPos, Level level) {
        AtomicBoolean result = new AtomicBoolean(true);
        // 检查结构能不能放
        structureBlocks.forEach((blockPos1, block) -> {
            BlockState blockState = block.defaultBlockState();
            if (blockState.isEmpty()) return;
            result.set(result.get() & level.getBlockState(blockPos.offset(blockPos1)).canBeReplaced());
        });

        return result.get();
    }
}
