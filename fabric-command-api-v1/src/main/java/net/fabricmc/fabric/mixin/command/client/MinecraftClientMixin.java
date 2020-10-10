package net.fabricmc.fabric.mixin.command.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(RunArgs args, CallbackInfo info) {
		ClientCommandInternals.buildDispatchers();
	}
}
