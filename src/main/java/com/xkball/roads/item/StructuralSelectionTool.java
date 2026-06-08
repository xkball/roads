package com.xkball.roads.item;

import com.xkball.roads.datacomponents.ModDataComponents;
import com.xkball.roads.datacomponents.StructuralSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class StructuralSelectionTool extends Item {
    public StructuralSelectionTool(Properties properties) {
        super(properties);
    }

    @Override
    @NonNull
    @SuppressWarnings("ConstantConditions")
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        ItemStack itemInHand = context.getItemInHand();
        Player player = context.getPlayer();
        ResourceKey<Level> dimension = context.getLevel().dimension();
        if (player == null)
            return InteractionResult.FAIL;

        if (hand == InteractionHand.MAIN_HAND) {
            if (itemInHand.get(ModDataComponents.STRUCTURAL_SELECTION.get()) == null) {
                //初始化
                itemInHand.set(ModDataComponents.STRUCTURAL_SELECTION.get(), new StructuralSelection(new GlobalPos(dimension,clickedPos), new GlobalPos(dimension,clickedPos)));
            }
            if (!player.isShiftKeyDown()) {
                itemInHand.set(ModDataComponents.STRUCTURAL_SELECTION.get(), new StructuralSelection(new GlobalPos(dimension,clickedPos), itemInHand.get(ModDataComponents.STRUCTURAL_SELECTION.get()).globalPos2()));
            } else {
                itemInHand.set(ModDataComponents.STRUCTURAL_SELECTION.get(), new StructuralSelection(itemInHand.get(ModDataComponents.STRUCTURAL_SELECTION.get()).globalPos1(), new GlobalPos(dimension,clickedPos)));
            }
        }

        return InteractionResult.FAIL;
    }

}
