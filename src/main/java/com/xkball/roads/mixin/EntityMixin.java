package com.xkball.roads.mixin;

import com.xkball.roads.collision.TriangleEntityCollision;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "collide", at = @At("RETURN"), cancellable = true)
    private void roads$collide(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        Vec3 result = TriangleEntityCollision.collide((Entity) (Object) this, cir.getReturnValue());
        cir.setReturnValue(result);
    }
}
