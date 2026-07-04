package com.xkball.roads.block.entity;

import com.xkball.roads.block.ModBlocks;
import com.xkball.roads.block.RoadBuilderBlock;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public class RoadBuilderBlockEntity extends BlockEntity {
    private final List<Vector3f> controlPoints = new ArrayList<>();

    public RoadBuilderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.ROAD_BUILDER_BLOCK_ENTITY.get(), pos, blockState);
        resetControlPointsTest();
    }

    public List<Vector3f> getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(List<Vector3f> points) {
        controlPoints.clear();
        for (Vector3f point : points) {
            controlPoints.add(new Vector3f(point));
        }
        setChanged();
    }

    private void resetControlPoints(BlockState blockState) {
        controlPoints.clear();
        Direction facing = blockState.getValue(RoadBuilderBlock.FACING);
        Direction back = facing.getOpposite();
        Direction.Axis axis = facing.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        int centerX = back.getStepX() * 4;
        int centerZ = back.getStepZ() * 4;
        addControlPoint(centerX, centerZ, axis, -2);
        addControlPoint(centerX, centerZ, axis, -1);
        addControlPoint(centerX, centerZ, axis, 1);
        addControlPoint(centerX, centerZ, axis, 2);
    }

    private void addControlPoint(int centerX, int centerZ, Direction.Axis axis, int offset) {
        if (axis == Direction.Axis.X) {
            controlPoints.add(new Vector3f(centerX + offset, 0, centerZ));
        } else {
            controlPoints.add(new Vector3f(centerX, 0, centerZ + offset));
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int size = input.getIntOr("controlPointSize", 0);
        controlPoints.clear();
        for (int i = 0; i < size; i++) {
            float x = input.getFloatOr("controlPoint" + i + "x", 0);
            float y = input.getFloatOr("controlPoint" + i + "y", 0);
            float z = input.getFloatOr("controlPoint" + i + "z", 0);
            controlPoints.add(new Vector3f(x, y, z));
        }
        if (controlPoints.isEmpty()) {
            resetControlPointsTest();
        }
    }

    private void resetControlPointsTest() {
        controlPoints.clear();
        RandomSource random = RandomSource.create();
        for (int i = 0; i < 10; i++) {
            controlPoints.add(new Vector3f(random.nextFloat() * 16 - 8, random.nextFloat() * 16 - 8, random.nextFloat() * 16 - 8));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("controlPointSize", controlPoints.size());
        for (int i = 0; i < controlPoints.size(); i++) {
            Vector3f point = controlPoints.get(i);
            output.putFloat("controlPoint" + i + "x", point.x());
            output.putFloat("controlPoint" + i + "y", point.y());
            output.putFloat("controlPoint" + i + "z", point.z());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
