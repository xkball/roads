package com.xkball.roads.blockentity;

import com.xkball.roads.Roads;
import com.xkball.roads.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Roads.MODID);
    // ? extends BaseRoadsBlockEntity
    public static final DeferredRegister<BlockEntityType<?>> BASE_BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Roads.MODID);

    public static final Supplier<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("test_block_entity", () ->
            new BlockEntityType<>(
                    TestBlockEntity::new,
                    false,
                    ModBlocks.TEST_BLOCK.get()));

    public static final Supplier<BlockEntityType<FakeMainBlockEntity>> FAKE_MAIN_BLOCK_ENTITY = BLOCK_ENTITIES.register("fake_main_block_entity", () ->
            new BlockEntityType<>(
                    FakeMainBlockEntity::new,
                    false,
                    ModBlocks.FAKE_MAIN_BLOCK.get()));

    public static final Supplier<BlockEntityType<FakeStructureBlockEntity>> FAKE_STRUCTURE_BLOCK_ENTITY = BLOCK_ENTITIES.register("fake_structure_block_entity", () ->
            new BlockEntityType<>(
                    FakeStructureBlockEntity::new,
                    false,
                    ModBlocks.FAKE_STRUCTURE_BLOCK.get()));

    public static final Supplier<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BLOCK_ENTITY = ModBlockEntities.registerBaseBlockEntity("generator_block_entity", () ->
            new BlockEntityType<>(
                    GeneratorBlockEntity::new,
                    false,
                    ModBlocks.GENERATOR_BLOCK.get()
            ));

    public static <T extends BaseRoadsBlockEntity> Supplier<BlockEntityType<T>> registerBaseBlockEntity(String name, Supplier<BlockEntityType<T>> sup) {
        return BASE_BLOCK_ENTITIES.register(name, sup);
    }
}
