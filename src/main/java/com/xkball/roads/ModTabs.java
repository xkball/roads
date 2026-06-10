package com.xkball.roads;

import com.xkball.roads.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Roads.MODID)
public class ModTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "roads" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Roads.MODID);

    // Creates a creative tab with the id "roads:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.roads"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.STRUCTURAL_SELECTION_TOOL.get().getDefaultInstance())
            .build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == EXAMPLE_TAB.getKey()) {
            ModItems.ITEMS.getEntries().forEach(item -> event.accept(item.get()));
            removeItem(event, ModItems.STRUCTURAL_SELECTION_TOOL);
        }
    }

    private static void removeItem(BuildCreativeModeTabContentsEvent event, ItemLike item) {
        event.remove(new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
