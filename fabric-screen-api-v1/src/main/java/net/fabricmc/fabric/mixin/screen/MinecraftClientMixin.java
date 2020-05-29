package net.fabricmc.fabric.mixin.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.world.level.LevelInfo;

import net.fabricmc.fabric.api.client.screen.v1.ScreenContext;
import net.fabricmc.fabric.api.client.screen.v1.ScreenTickCallback;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow
	public Screen currentScreen;

	// Should be caught by "Screen#wrapScreenError" if anything fails
	@Inject(method = "method_1572", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onTickScreen(CallbackInfo ci, Screen screen) {
		ScreenTickCallback.EVENT.invoker().onTick((MinecraftClient) (Object) this, screen, (ScreenContext) screen);
	}

	// This is the odd screen that isn't ticked by the main tick loop, so we fire events for this screen.
	@Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;render(Z)V"))
	private void onTickLoadingScreen(String name, String displayName, LevelInfo levelInfo, CallbackInfo ci) {
		final Screen currentScreen = this.currentScreen;
		ScreenTickCallback.EVENT.invoker().onTick((MinecraftClient) (Object) this, currentScreen, (ScreenContext) currentScreen);
	}
}
