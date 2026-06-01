package com.xkball.roads.block;

import com.xkball.roads.multi.Structure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;

public abstract class RoadBaseStructureEntityBlock extends RoadsBaseEntityBlock {
    public RoadBaseStructureEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    protected InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        Structure structure = getStructure();
        //TODO 根据结构信息自动放置方块

        structure.getStructure().forEach(System.out::println);

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    abstract public Structure getStructure();
}
