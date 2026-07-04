package com.xkball.roads.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xkball.roads.block.entity.RoadBuilderBlockEntity;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public class RoadBuilderBlockEntityRenderer implements BlockEntityRenderer<RoadBuilderBlockEntity, RoadBuilderBlockEntityRenderer.RenderState> {
    private static final int SAMPLES_PER_CURVE = 16;
    private static final int CURVE_COLOR = 0xFFFFAA00;

    public RoadBuilderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(RoadBuilderBlockEntity blockEntity, RenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.controlPoints.clear();
        for (Vector3f point : blockEntity.getControlPoints()) {
            state.controlPoints.add(new Vector3f(point));
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.controlPoints.size() < 4) {
            return;
        }
        float width = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth;
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, buffer) -> renderCurves(state.controlPoints, pose, buffer, width));
    }

    private static void renderCurves(List<Vector3f> controlPoints, PoseStack.Pose pose, VertexConsumer buffer, float width) {
        for (int i = 0; i + 4 <= controlPoints.size(); i += 2) {
            Vector3f previous = hermite(0, controlPoints.get(i), controlPoints.get(i + 1), controlPoints.get(i + 2), controlPoints.get(i + 3));
            for (int sample = 1; sample <= SAMPLES_PER_CURVE; sample++) {
                float t = sample / (float) SAMPLES_PER_CURVE;
                Vector3f current = hermite(t, controlPoints.get(i), controlPoints.get(i + 1), controlPoints.get(i + 2), controlPoints.get(i + 3));
                putLine(buffer, pose, previous, current, width);
                previous = current;
            }
        }
    }

    private static Vector3f hermite(float t, Vector3f pointA, Vector3f tangentA, Vector3f pointB, Vector3f tangentB) {
        float t2 = t * t;
        float t3 = t2 * t;
        float h00 = 2 * t3 - 3 * t2 + 1;
        float h10 = t3 - 2 * t2 + t;
        float h01 = -2 * t3 + 3 * t2;
        float h11 = t3 - t2;
        return new Vector3f(
                h00 * pointA.x() + h10 * tangentA.x() + h01 * pointB.x() + h11 * tangentB.x(),
                h00 * pointA.y() + h10 * tangentA.y() + h01 * pointB.y() + h11 * tangentB.y(),
                h00 * pointA.z() + h10 * tangentA.z() + h01 * pointB.z() + h11 * tangentB.z()
        );
    }

    private static void putLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f start, Vector3f end, float width) {
        Vector3f normal = new Vector3f(end).sub(start);
        if (normal.lengthSquared() == 0) {
            return;
        }
        normal.normalize();
        buffer.addVertex(pose, start.x(), start.y(), start.z()).setColor(CURVE_COLOR).setNormal(pose, normal).setLineWidth(width);
        buffer.addVertex(pose, end.x(), end.y(), end.z()).setColor(CURVE_COLOR).setNormal(pose, normal).setLineWidth(width);
    }

    public static class RenderState extends BlockEntityRenderState {
        public final List<Vector3f> controlPoints = new ArrayList<>();
    }
}
