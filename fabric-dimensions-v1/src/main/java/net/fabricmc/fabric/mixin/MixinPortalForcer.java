package net.fabricmc.fabric.mixin;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalForcer.class)
public abstract class MixinPortalForcer {
    @Shadow @Final private ServerWorld world;

    @Inject(method = "usePortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getLastPortalDirectionVector()Lnet/minecraft/util/math/Vec3d;"))
    private void onUsePortal(Entity teleported, float yaw, CallbackInfoReturnable<Boolean> cir) {
        FabricDimensionInternals.prepareDimensionalTeleportation(teleported);
    }

    @Inject(method = "getPortal", at = @At("HEAD"), cancellable = true)
    private void findEntityPlacement(BlockPos pos, Vec3d velocity, Direction portalDir, double portalX, double portalY, boolean player, CallbackInfoReturnable<BlockPattern.TeleportTarget> cir) {
        BlockPattern.TeleportTarget ret = FabricDimensionInternals.tryFindPlacement(this.world, portalDir, portalX, portalY);
        if (ret != null) {
            cir.setReturnValue(ret);
        }
    }
}
