package com.xkball.roads.block;

import com.mojang.logging.LogUtils;
import com.xkball.roads.Roads;
import com.xkball.roads.block.collidetest.QuadCollection;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class CollideTestBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier MODEL_LOCATION = Identifier.fromNamespaceAndPath(Roads.MODID, "models/block/collide_test");
    private static QuadCollection cachedQuads = null;

    private static final Set<String> FACE_NAMES = Set.of("north", "south", "east", "west", "up", "down");

    public CollideTestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

 
    
}
