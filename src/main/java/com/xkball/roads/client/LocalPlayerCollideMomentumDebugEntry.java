package com.xkball.roads.client;

import java.util.Locale;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class LocalPlayerCollideMomentumDebugEntry implements DebugScreenEntry {
    
    public static Vec3 last = Vec3.ZERO;
    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        displayer.addLine(String.format(Locale.ROOT, "LocalPlayer collide momentum: %.5f,%.5f,%.5f", last.x, last.y, last.z));
    }
}
