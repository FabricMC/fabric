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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.api.client.screen.v1.FabricScreen;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow
	public Screen currentScreen;

	@Unique
	private FabricScreen tickingScreen;

	// These two injections should be caught by "Screen#wrapScreenError" if anything fails in an event and then rethrown in the crash report
	@Inject(method = "method_1572", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;tick()V"))
	private void beforeScreenTick(CallbackInfo ci) {
		// Store the screen in a variable in case someone tries to change the screen during this before tick event.
		// If someone changes the screen, the after tick event will likely have class cast exceptions or an NPE.
		this.tickingScreen = (FabricScreen) this.currentScreen;
		this.tickingScreen.getBeforeTickEvent().invoker().beforeTick((MinecraftClient) (Object) this, this.tickingScreen.getScreen(), this.tickingScreen);
	}

	@Inject(method = "method_1572", at = @At("TAIL"))
	private void afterScreenTick(CallbackInfo ci) {
		this.tickingScreen.getAfterTickEvent().invoker().afterTick((MinecraftClient) (Object) this, this.tickingScreen.getScreen(), this.tickingScreen);
		// Finally set the currently ticking screen to null
		this.tickingScreen = null;
	}

	// The LevelLoadingScreen is the odd screen that isn't ticked by the main tick loop, so we fire events for this screen.
	// We Coerce the package-private inner class representing the world load action so we don't need an access widener.
	@Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;tick()V"))
	private void beforeLoadingScreenTick(String worldName, DynamicRegistryManager.Impl dynamicRegistryManager, Function<LevelStorage.Session, DataPackSettings> function, Function4<LevelStorage.Session, DynamicRegistryManager.Impl, ResourceManager, DataPackSettings, SaveProperties> function4, boolean safeMode, @Coerce Object worldLoadAction, CallbackInfo ci) {
		// Store the screen in a variable in case someone tries to change the screen during this before tick event.
		// If someone changes the screen, the after tick event will likely have class cast exceptions or an NPE.
		this.tickingScreen = (FabricScreen) this.currentScreen;
		this.tickingScreen.getBeforeTickEvent().invoker().beforeTick((MinecraftClient) (Object) this, this.tickingScreen.getScreen(), this.tickingScreen);
	}

	@Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;render(Z)V"))
	private void afterLoadingScreenTick(String worldName, DynamicRegistryManager.Impl dynamicRegistryManager, Function<LevelStorage.Session, DataPackSettings> function, Function4<LevelStorage.Session, DynamicRegistryManager.Impl, ResourceManager, DataPackSettings, SaveProperties> function4, boolean safeMode, @Coerce Object worldLoadAction, CallbackInfo ci) {
		this.tickingScreen.getAfterTickEvent().invoker().afterTick((MinecraftClient) (Object) this, this.tickingScreen.getScreen(), this.tickingScreen);
		// Finally set the currently ticking screen to null
		this.tickingScreen = null;
	}
}
