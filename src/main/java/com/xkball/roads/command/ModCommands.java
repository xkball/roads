package com.xkball.roads.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.xkball.roads.Roads;
import com.xkball.roads.datacomponents.ModDataComponents;
import com.xkball.roads.datacomponents.StructuralSelection;
import com.xkball.roads.item.ModItems;
import com.xkball.roads.multi.StructureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = Roads.MODID)
public class ModCommands {
    @SubscribeEvent
    public static void onCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(Roads.MODID)
                .then(Commands.literal("structural_selection")
                        .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .then(Commands.argument("structure_name", StringArgumentType.string())
                                .executes(context -> {
                                    LocalPlayer player = Minecraft.getInstance().player;
                                    if (player != null) {
                                        ItemStack mainHandItem = player.getMainHandItem();
                                        Item item = mainHandItem.getItem();
                                        if (item == ModItems.STRUCTURAL_SELECTION_TOOL.get()) {
                                            StructuralSelection defaultValue = new StructuralSelection(
                                                    new BlockPos(0, 0, 0),
                                                    new BlockPos(0, 0, 0));
                                            BlockPos blockPos1 = mainHandItem
                                                    .getOrDefault(ModDataComponents.STRUCTURAL_SELECTION.get(),
                                                            defaultValue)
                                                    .blockPos1();
                                            BlockPos blockPos2 = mainHandItem
                                                    .getOrDefault(ModDataComponents.STRUCTURAL_SELECTION.get(),
                                                            defaultValue)
                                                    .blockPos2();
                                            AABB aabb = AABB.encapsulatingFullBlocks(blockPos1, blockPos2);
                                            String structure = StructureUtil.blocksToStructure(player.level(), aabb, StringArgumentType.getString(context, "structure_name"));
                                            System.out.println(structure);
                                            Minecraft.getInstance().keyboardHandler.setClipboard(structure);
                                        }

                                    }
                                    return Command.SINGLE_SUCCESS;
                                }))));
    }
}
