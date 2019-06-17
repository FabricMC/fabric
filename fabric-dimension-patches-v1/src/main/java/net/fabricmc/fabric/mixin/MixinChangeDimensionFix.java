package net.fabricmc.fabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;

@Mixin(value = PortalForcer.class, priority = -100000)
public class MixinChangeDimensionFix
{
	@Shadow
	@Final
	private ServerWorld world;

	@Inject(at = @At("HEAD"), method = "usePortal", cancellable = true)
	private void usePortal(final Entity entity, final float yaw, final CallbackInfoReturnable<Boolean> info) {
		if(entity.method_5656() == null) {
			BlockPos topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(entity.getPos().getX(), 0, entity.getPos().getZ()));

			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity) entity).networkHandler.teleportRequest(topPos.getX(), topPos.getY(), topPos.getZ(), 0, 0, new HashSet<>());
				((ServerPlayerEntity) entity).networkHandler.syncWithPlayerPosition();
			} else {
				entity.setPosition(topPos.getX(), topPos.getY(), topPos.getZ());
			}

			info.setReturnValue(true);
			info.cancel();
		}
	}
}
