package com.xkball.roads.datagen.provider;

import com.xkball.roads.block.ModBlocks;
import com.xkball.roads.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import org.jspecify.annotations.NonNull;

public class ModItemModelProvider extends ModelProvider {
    public ModItemModelProvider(PackOutput output, String modId) {
        super(output, modId);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
        // Generate models and associated files here
        itemModels.generateFlatItem(ModItems.EXAMPLE_ITEM.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.STRUCTURAL_SELECTION_TOOL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        blockModels.createTrivialCube(ModBlocks.EXAMPLE_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.TEST_BLOCK.get());
        blockModels.registerSimpleFlatItemModel(ModBlocks.EXAMPLE_BLOCK.get());


    }
}
