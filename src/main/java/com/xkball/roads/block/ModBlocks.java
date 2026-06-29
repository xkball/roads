package com.xkball.roads.block;

import com.xkball.roads.Roads;
import com.xkball.roads.block.entity.RoadBuilderBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Roads.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Roads.MODID);
    
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", p -> p.mapColor(MapColor.STONE));
    public static final DeferredBlock<RoadBuilderBlock> ROAD_BUILDER = BLOCKS.registerBlock(
            "road_builder",
            RoadBuilderBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RoadBuilderBlockEntity>> ROAD_BUILDER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "road_builder",
            () -> new BlockEntityType<>(RoadBuilderBlockEntity::new, ROAD_BUILDER.get())
    );
}
