package com.xkball.roads.item;

import com.xkball.roads.datacomponents.ModDataComponents;
import com.xkball.roads.datacomponents.StructuralSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jspecify.annotations.NonNull;

public class StructuralSelectionTool extends Item {
    public StructuralSelectionTool(Properties properties) {
        super(properties);
    }

    @Override
    @NonNull
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        ItemStack itemInHand = context.getItemInHand();
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.FAIL;

        if (hand == InteractionHand.MAIN_HAND) {
            if (itemInHand.get(ModDataComponents.STRUCTURAL_SELECTION.get()) == null) {
                //初始化
                itemInHand.set(ModDataComponents.STRUCTURAL_SELECTION.get(), new StructuralSelection(clickedPos, clickedPos));
            }
            if (!player.isShiftKeyDown()) {
                itemInHand.set(ModDataComponents.STRUCTURAL_SELECTION.get(), new StructuralSelection(clickedPos, itemInHand.get(ModDataComponents.STRUCTURAL_SELECTION.get()).blockPos2()));
            } else {
                itemInHand.set(ModDataComponents.STRUCTURAL_SELECTION.get(), new StructuralSelection(itemInHand.get(ModDataComponents.STRUCTURAL_SELECTION.get()).blockPos1(), clickedPos));
            }
        }

        return InteractionResult.FAIL;
    }

}
