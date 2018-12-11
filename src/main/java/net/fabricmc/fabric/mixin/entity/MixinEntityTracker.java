package net.fabricmc.fabric.mixin.entity;

import net.fabricmc.fabric.entity.EntityTrackingRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTracker.class)
public abstract class MixinEntityTracker {
	@Shadow
	public abstract void add(Entity var1, int var2, int var3, boolean var4);

	@Inject(at = @At("HEAD"), method = "add", cancellable = true)
	public void add(Entity entity, CallbackInfo info) {
		if (entity != null) {
			EntityTrackingRegistry.Entry entry = EntityTrackingRegistry.INSTANCE.get(entity.getType());
			if (entry != null) {
				add(entity, entry.getTrackingDistance(), entry.getUpdateIntervalTicks(), entry.alwaysUpdateVelocity());
				info.cancel();
			}
		}
	}
}
