package net.fabricmc.fabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityHooks {
    @Accessor
    void setLastPortalDirectionVector(Vec3d vec);

    @Accessor
    void setLastPortalDirection(Direction dir);

}
