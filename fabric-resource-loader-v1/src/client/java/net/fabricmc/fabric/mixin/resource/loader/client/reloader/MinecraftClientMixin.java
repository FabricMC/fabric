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

package net.fabricmc.fabric.mixin.resource.loader.client.reloader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderHolder;
import net.fabricmc.fabric.api.resource.loader.v1.client.ClientResourceReloadEvents;
import net.fabricmc.fabric.impl.resource.loader.client.reloader.ClientReloadContext;
import net.fabricmc.fabric.impl.resource.loader.client.reloader.ClientReloadRegisterContext;
import net.fabricmc.fabric.impl.resource.loader.reloader.LegacyReloaderHolder;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements ResourceReloaderHolder {
	@Final
	@Shadow
	private ReloadableResourceManagerImpl resourceManager;

	/**
	 * Inject at the first reload towards the end of the constructor to set up modded resource reloaders.
	 */
	@Inject(
			method = "<init>",
			at = @At(value = "INVOKE", target = "net/minecraft/resource/ReloadableResourceManagerImpl.reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resource/ResourceReload;"),
			allow = 1
	)
	private void onFirstReload(RunArgs args, CallbackInfo ci) {
		ClientReloadRegisterContext context = new ClientReloadRegisterContext();
		// Add vanilla reloaders
		context.reloaderSorting.addVanillaReloaders(resourceManager.reloaders);
		// Gather modded reloaders
		context.reloaderSorting.addLegacyReloaders(LegacyReloaderHolder.CLIENT_DEFINITIONS);
		ClientResourceReloadEvents.REGISTER_RELOADERS.invoker().registerResourceReloaders(context);
		// Add modded reloaders to map
		context.reloaderSorting.addReloadersToMap(fabric_reloaders);
		// Sort all reloaders and place them in the resource manager
		resourceManager.reloaders.clear();
		resourceManager.reloaders.addAll(context.reloaderSorting.getSortedReloaders());
	}

	@Unique
	private final Map<Identifier, ResourceReloader> fabric_reloaders = new HashMap<>();

	@Override
	public ResourceReloader getResourceReloader(Identifier identifier) {
		return fabric_reloaders.get(identifier);
	}

	/**
	 * Fire end reload event for the first reload.
	 */
	@Inject(
			method = "method_24040",
			at = @At("HEAD")
	)
	private void onFirstReloadEnd(Optional<Throwable> error, CallbackInfo ci) {
		ClientResourceReloadEvents.END_RELOAD.invoker().onEndResourceReload(new ClientReloadContext(), error.isEmpty());
	}

	/**
	 * Fire end reload event for subsequent reloads.
	 */
	@Inject(
			method = "method_24228",
			at = @At("HEAD")
	)
	private void onReloadEnd(boolean force, CompletableFuture<?> completableFuture, Optional<Throwable> error, CallbackInfo ci) {
		ClientResourceReloadEvents.END_RELOAD.invoker().onEndResourceReload(new ClientReloadContext(), error.isEmpty());
	}
}
