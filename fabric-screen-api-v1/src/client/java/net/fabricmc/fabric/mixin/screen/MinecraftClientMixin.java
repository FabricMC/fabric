/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.screen;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {
	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-screen-api-v1");
	@Unique
	private static final boolean DEBUG_SCREEN = FabricLoader.getInstance().isDevelopmentEnvironment() || Boolean.getBoolean("fabric.debugScreen");

	@Shadow
	public Screen currentScreen;

	@Shadow
	private Thread thread;
	@Unique
	private Screen tickingScreen;

	@Inject(method = "setScreen", at = @At("HEAD"))
	private void checkThreadOnDev(@Nullable Screen screen, CallbackInfo ci) {
		Thread currentThread = Thread.currentThread();

		if (DEBUG_SCREEN && currentThread != this.thread) {
			LOGGER.error("Attempted to set screen to \"{}\" outside the render thread (\"{}\"). This will likely follow a crash! Make sure to call setScreen on the render thread.", screen, currentThread.getName());
		}
	}

	@Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;removed()V", shift = At.Shift.AFTER))
	private void onScreenRemove(@Nullable Screen screen, CallbackInfo ci) {
		ScreenEvents.remove(this.currentScreen).invoker().onRemove(this.currentScreen);
	}

	@Inject(method = "stop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;removed()V", shift = At.Shift.AFTER))
	private void onScreenRemoveBecauseStopping(CallbackInfo ci) {
		ScreenEvents.remove(this.currentScreen).invoker().onRemove(this.currentScreen);
	}

	// Synthetic method in `tick`
	// These two injections should be caught by "Screen#wrapScreenError" if anything fails in an event and then rethrown in the crash report
	@Inject(method = "method_1572", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;tick()V"))
	private void beforeScreenTick(CallbackInfo ci) {
		// Store the screen in a variable in case someone tries to change the screen during this before tick event.
		// If someone changes the screen, the after tick event will likely have class cast exceptions or an NPE.
		this.tickingScreen = this.currentScreen;
		ScreenEvents.beforeTick(this.tickingScreen).invoker().beforeTick(this.tickingScreen);
	}

	// Synthetic method in `tick`
	@Inject(method = "method_1572", at = @At("TAIL"))
	private void afterScreenTick(CallbackInfo ci) {
		ScreenEvents.afterTick(this.tickingScreen).invoker().afterTick(this.tickingScreen);
		// Finally set the currently ticking screen to null
		this.tickingScreen = null;
	}

	// The LevelLoadingScreen is the odd screen that isn't ticked by the main tick loop, so we fire events for this screen.
	// We Coerce the package-private inner class representing the world load action so we don't need an access widener.
	@Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/resource/ResourcePackManager;Lnet/minecraft/server/SaveLoader;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;tick()V"))
	private void beforeLoadingScreenTick(CallbackInfo ci) {
		// Store the screen in a variable in case someone tries to change the screen during this before tick event.
		// If someone changes the screen, the after tick event will likely have class cast exceptions or throw a NPE.
		this.tickingScreen = this.currentScreen;
		ScreenEvents.beforeTick(this.tickingScreen).invoker().beforeTick(this.tickingScreen);
	}

	@Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/resource/ResourcePackManager;Lnet/minecraft/server/SaveLoader;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;render(Z)V"))
	private void afterLoadingScreenTick(CallbackInfo ci) {
		ScreenEvents.afterTick(this.tickingScreen).invoker().afterTick(this.tickingScreen);
		// Finally set the currently ticking screen to null
		this.tickingScreen = null;
	}
}
