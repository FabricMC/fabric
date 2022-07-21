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

package net.fabricmc.fabric.mixin.resource.loader.client;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.SaveLoading;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.fabricmc.fabric.mixin.resource.loader.ResourcePackManagerAccessor;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
	@Unique
	private static DataPackSettings defaultDataPackSettings;

	@Shadow
	private ResourcePackManager packManager;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	public MoreOptionsDialog moreOptionsDialog;

	@Shadow
	protected DataPackSettings dataPackSettings;

	@Shadow
	private static SaveLoading.ServerConfig createServerConfig(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings) {
		return null;
	}

	@Shadow
	@Nullable
	protected abstract Pair<File, ResourcePackManager> getScannedPack();

	private CreateWorldScreenMixin() {
		super(null);
	}

	@ModifyVariable(method = "create(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;createServerConfig(Lnet/minecraft/resource/ResourcePackManager;Lnet/minecraft/resource/DataPackSettings;)Lnet/minecraft/server/SaveLoading$ServerConfig;"))
	private static ResourcePackManager onCreateResManagerInit(ResourcePackManager manager) {
		// Add mod data packs to the initial res pack manager so they are active even if the user doesn't use custom data packs
		((ResourcePackManagerAccessor) manager).getProviders().add(new ModResourcePackCreator(ResourceType.SERVER_DATA));
		return manager;
	}

	@Redirect(method = "create(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/resource/DataPackSettings;SAFE_MODE:Lnet/minecraft/resource/DataPackSettings;", ordinal = 0))
	private static DataPackSettings replaceDefaultSettings() {
		return (defaultDataPackSettings = ModResourcePackUtil.createDefaultDataPackSettings());
	}

	@ModifyArg(method = "create(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;<init>(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/resource/DataPackSettings;Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;)V"), index = 1)
	private static DataPackSettings useReplacedDefaultSettings(DataPackSettings dataPackSettings) {
		return defaultDataPackSettings;
	}

	@Redirect(method = "method_41854", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/DynamicRegistryManager$Mutable;toImmutable()Lnet/minecraft/util/registry/DynamicRegistryManager$Immutable;"))
	private static DynamicRegistryManager.Immutable loadDynamicRegistry(DynamicRegistryManager.Mutable mutableRegistryManager, ResourceManager dataPackManager) {
		// This loads the dynamic registry from the data pack
		RegistryOps.ofLoaded(JsonOps.INSTANCE, mutableRegistryManager, dataPackManager);
		return mutableRegistryManager.toImmutable();
	}

	/**
	 * Load the DynamicRegistryManager again to fix GeneratorOptions not having custom dimensions.
	 * Taken from {@link CreateWorldScreen#applyDataPacks(ResourcePackManager)}.
	 */
	@SuppressWarnings("JavadocReference")
	@Inject(method = "startServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;clearDataPackTempDir()V"))
	private void loadDatapackDimensions(CallbackInfo ci) {
		CompletableFuture<Void> future = SaveLoading.load(createServerConfig(getScannedPack().getSecond(), dataPackSettings), (resourceManager, dataPackSettings1) -> {
			GeneratorOptionsHolder holder = moreOptionsDialog.getGeneratorOptionsHolder();
			DynamicRegistryManager.Mutable newDrm = DynamicRegistryManager.createAndLoad();
			DynamicOps<JsonElement> heldOps = RegistryOps.of(JsonOps.INSTANCE, holder.dynamicRegistryManager());
			DynamicOps<JsonElement> newOps = RegistryOps.ofLoaded(JsonOps.INSTANCE, newDrm, resourceManager);
			DataResult<GeneratorOptions> result = GeneratorOptions.CODEC.encodeStart(heldOps, holder.generatorOptions())
					.flatMap(json -> GeneratorOptions.CODEC.parse(newOps, json));
			return Pair.of(result, newDrm.toImmutable());
		}, (resourceManager, dataPackContents, drm, result) -> {
			resourceManager.close();
			GeneratorOptions options = result.getOrThrow(false, Util.addPrefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error));
			GeneratorOptionsHolder holder = new GeneratorOptionsHolder(options, result.lifecycle().add(drm.getRegistryLifecycle()), drm, dataPackContents);
			((MoreOptionsDialogAccessor) moreOptionsDialog).callSetGeneratorOptionsHolder(holder);
			return null;
		}, Util.getMainWorkerExecutor(), client);

		client.runTasks(future::isDone);
	}

	@Inject(method = "getScannedPack",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackManager;scanPacks()V", shift = At.Shift.BEFORE))
	private void onScanPacks(CallbackInfoReturnable<Pair<File, ResourcePackManager>> cir) {
		// Allow to display built-in data packs in the data pack selection screen at world creation.
		((ResourcePackManagerAccessor) this.packManager).getProviders().add(new ModResourcePackCreator(ResourceType.SERVER_DATA));
	}
}
