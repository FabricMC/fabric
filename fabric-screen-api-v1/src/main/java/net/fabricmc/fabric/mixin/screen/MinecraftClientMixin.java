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

import java.util.function.Function;

import com.mojang.datafixers.util.Function4;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.api.client.screen.v1.FabricScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow
	public Screen currentScreen;

	// These two should be caught by "Screen#wrapScreenError" if anything fails and then rethrown in the crash report

	@Inject(method = "method_1572", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;tick()V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void beforeScreenTick(CallbackInfo ci) {
		ScreenEvents.BEFORE_TICK.invoker().beforeTick((MinecraftClient) (Object) this, this.currentScreen, (FabricScreen) this.currentScreen);
	}

	@Inject(method = "method_1572", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void afterScreenTick(CallbackInfo ci) {
		ScreenEvents.AFTER_TICK.invoker().afterTick((MinecraftClient) (Object) this, this.currentScreen, (FabricScreen) this.currentScreen);
	}

	// The LevelLoadingScreen is the odd screen that isn't ticked by the main tick loop, so we fire events for this screen.
	// We Coerce the package-private inner class representing the world load action.

	@Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;tick()V"))
	private void beforeLoadingScreenTick(String worldName, DynamicRegistryManager.Impl dynamicRegistryManager, Function<LevelStorage.Session, DataPackSettings> function, Function4<LevelStorage.Session, DynamicRegistryManager.Impl, ResourceManager, DataPackSettings, SaveProperties> function4, boolean safeMode, @Coerce Object worldLoadAction, CallbackInfo ci) {
		final Screen currentScreen = this.currentScreen;
		ScreenEvents.BEFORE_TICK.invoker().beforeTick((MinecraftClient) (Object) this, currentScreen, (FabricScreen) currentScreen);
	}

	@Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;render(Z)V"))
	private void afterLoadingScreenTick(String worldName, DynamicRegistryManager.Impl dynamicRegistryManager, Function<LevelStorage.Session, DataPackSettings> function, Function4<LevelStorage.Session, DynamicRegistryManager.Impl, ResourceManager, DataPackSettings, SaveProperties> function4, boolean safeMode, @Coerce Object worldLoadAction, CallbackInfo ci) {
		final Screen currentScreen = this.currentScreen;
		ScreenEvents.AFTER_TICK.invoker().afterTick((MinecraftClient) (Object) this, currentScreen, (FabricScreen) currentScreen);
	}
}
