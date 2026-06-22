package com.xkball.roads.mixin;

import com.xkball.roads.client.LocalPlayerCollideMomentumDebugEntry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Entity  {
    
    
    public LocalPlayerMixin(EntityType<?> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected Vec3 collide(Vec3 movement) {
        LocalPlayerCollideMomentumDebugEntry.last = movement;
        return super.collide(movement);
    }
    
}
