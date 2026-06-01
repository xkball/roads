package com.xkball.roads.blockentity;

import com.xkball.roads.Roads;
import com.xkball.roads.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Roads.MODID);

    public static final Supplier<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("test_block_entity", () ->
            new BlockEntityType<>(
                    TestBlockEntity::new,
                    false,
                    ModBlocks.TEST_BLOCK.get()));
}
