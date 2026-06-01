package com.xkball.roads.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

public abstract class RoadsBaseEntityBlock extends Block implements EntityBlock {
    public RoadsBaseEntityBlock(Properties properties) {
        super(properties);
    }

    protected static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker
    ) {
        return expected == actual ? (BlockEntityTicker<A>)ticker : null;
    }
}
