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

package net.fabricmc.fabric.mixin.resource.loader.reloader;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderHolder;
import net.fabricmc.fabric.impl.resource.loader.reloader.ServerReloadContext;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ResourceReloaderHolder {
	@Shadow
	private MinecraftServer.ResourceManagerHolder resourceManagerHolder;

	/**
	 * Provides access to resource reloaders via the server directly, as the data pack contents are hard to access.
	 */
	@Override
	public ResourceReloader getResourceReloader(Identifier identifier) {
		return resourceManagerHolder.dataPackContents().getResourceReloader(identifier);
	}

	/**
	 * Captures the minecraft server starting the reload.
	 */
	@Inject(
			method = "reloadResources",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "net/minecraft/registry/CombinedDynamicRegistries.getPrecedingRegistryManagers(Ljava/lang/Object;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void captureMinecraftServer(Collection<String> dataPacks, CallbackInfoReturnable<CompletableFuture<Void>> cir, DynamicRegistryManager.Immutable drm) {
		ServerReloadContext.SERVER_BY_DRM.put(drm, (MinecraftServer) (Object) this);
	}
}
