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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderHolder;
import net.fabricmc.fabric.api.resource.loader.v1.ServerResourceReloadEvents;
import net.fabricmc.fabric.impl.resource.loader.reloader.LegacyReloaderHolder;
import net.fabricmc.fabric.impl.resource.loader.reloader.ServerReloadContext;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin implements ResourceReloaderHolder {
	@Unique
	private ServerReloadContext fabric_context;

	@Inject(
			method = "reload",
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/server/DataPackContents.getContents()Ljava/util/List;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void createReloadContext(ResourceManager manager, DynamicRegistryManager.Immutable dynamicRegistryManager, FeatureSet enabledFeatures, CommandManager.RegistrationEnvironment environment, int functionPermissionLevel, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<DataPackContents>> cir, DataPackContents dataPackContents) {
		((DataPackContentsMixin) (Object) dataPackContents).fabric_context = new ServerReloadContext(manager, dynamicRegistryManager, dataPackContents);
	}

	@Redirect(
			method = "reload",
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/server/DataPackContents.getContents()Ljava/util/List;"
			)
	)
	private static List<ResourceReloader> startReload(DataPackContents dataPackContents) {
		ServerReloadContext reloadContext = ((DataPackContentsMixin) (Object) dataPackContents).fabric_context;
		// Add vanilla reloaders
		reloadContext.reloaderSorting.addVanillaReloaders(dataPackContents.getContents());
		// Gather modded reloaders
		reloadContext.reloaderSorting.addLegacyReloaders(LegacyReloaderHolder.SERVER_DEFINITIONS);
		ServerResourceReloadEvents.REGISTER_RELOADERS.invoker().registerResourceReloaders(reloadContext);
		// Add modded reloaders to map
		reloadContext.reloaderSorting.addReloadersToMap(((DataPackContentsMixin) (Object) dataPackContents).fabric_reloaders);
		// Fire reload start event
		ServerResourceReloadEvents.START_RELOAD.invoker().onStartResourceReload(reloadContext);
		// Sort and return all reloaders
		return reloadContext.reloaderSorting.getSortedReloaders();
	}

	@Inject(
			method = "reload",
			at = @At("RETURN"),
			cancellable = true
	)
	private static void endReload(ResourceManager manager, DynamicRegistryManager.Immutable dynamicRegistryManager, FeatureSet enabledFeatures, CommandManager.RegistrationEnvironment environment, int functionPermissionLevel, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<DataPackContents>> cir) {
		cir.setReturnValue(cir.getReturnValue().whenComplete((dataPackContents, throwable) -> {
			// Fire reload end event
			ServerReloadContext reloadContext = ((DataPackContentsMixin) (Object) dataPackContents).fabric_context;
			ServerResourceReloadEvents.END_RELOAD.invoker().onEndResourceReload(reloadContext, throwable == null);
			// We don't need the context anymore
			((DataPackContentsMixin) (Object) dataPackContents).fabric_context = null;
		}));
	}

	@Unique
	private final Map<Identifier, ResourceReloader> fabric_reloaders = new HashMap<>();

	@Override
	public ResourceReloader getResourceReloader(Identifier identifier) {
		return fabric_reloaders.get(identifier);
	}
}
