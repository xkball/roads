package com.xkball.roads.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.roads.block.entity.RoadBuilderBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class RoadBuilderBlockEntityRenderer implements BlockEntityRenderer<RoadBuilderBlockEntity, BlockEntityRenderState> {
    public RoadBuilderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
    }
}
