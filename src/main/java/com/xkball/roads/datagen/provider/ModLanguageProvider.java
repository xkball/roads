package com.xkball.roads.datagen.provider;

import com.xkball.roads.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.roads","Roads");
        add(ModItems.EXAMPLE_ITEM.get(),"占位物品");
        add(ModItems.EXAMPLE_BLOCK_ITEM.get(),"占位方块");

        add(ModItems.TEST_BLOCK_ITEM.get(),"结构测试方块");
//        add(ModBlocks.EXAMPLE_BLOCK.get(),"占位方块");
    }
}
