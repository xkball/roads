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

        add(ModItems.TEST_BLOCK_ITEM.get(),"结构测试方块");
        add(ModItems.STRUCTURAL_SELECTION_TOOL.get(),"结构选区工具");
        add(ModItems.FAKE_MAIN_BLOCK_ITEM.get(),"假主方块");
        add(ModItems.FAKE_STRUCTURE_BLOCK_ITEM.get(),"假结构方块");
//        add(ModBlocks.EXAMPLE_BLOCK.get(),"占位方块");
    }
}
