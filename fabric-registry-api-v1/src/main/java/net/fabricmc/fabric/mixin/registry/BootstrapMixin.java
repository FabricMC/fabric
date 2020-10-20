package net.fabricmc.fabric.mixin.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.Bootstrap;

import net.fabricmc.fabric.impl.registry.RegistryAttributeTracking;

// Priority must be make this mixin apply after registry sync so state tracking logic can be setup.
@Mixin(value = Bootstrap.class, priority = 1001)
abstract class BootstrapMixin {
	@Inject(method = "setOutputStreams", at = @At("RETURN"))
	private static void initialize(CallbackInfo info) {
		RegistryAttributeTracking.bootstrapRegistries();
	}
}
