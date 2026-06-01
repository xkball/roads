package com.xkball.roads.block;

import com.xkball.roads.multi.Structure;

public abstract class RoadBaseStructureEntityBlock extends RoadsBaseEntityBlock {
    public RoadBaseStructureEntityBlock(Properties properties) {
        super(properties);
    }

    abstract public Structure getStructure();
}
