package com.xkball.roads.block;

import com.mojang.logging.LogUtils;

import com.xkball.xklibmc.annotation.NonNullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

@NonNullByDefault
public class CollideTestBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();

    public CollideTestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

 
    
}
