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

package net.fabricmc.fabric.mixin.registry.sync.client;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.impl.registry.sync.PersistentDynamicRegistryHandler;
import net.fabricmc.fabric.mixin.registry.sync.AccessorLevelStorageSession;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.fabricmc.fabric.impl.registry.sync.RemapException;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Unique
	private static Logger FABRIC_LOGGER = LogManager.getLogger();

	// Unmap the registry before loading a new SP/MP setup.
	@Inject(at = @At("RETURN"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
	public void disconnectAfter(Screen screen_1, CallbackInfo info) {
		try {
			RegistrySyncManager.unmap();
		} catch (RemapException e) {
			FABRIC_LOGGER.warn("Failed to unmap Fabric registries!", e);
		}
	}

	@Inject(method = "createSaveProperties", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/dynamic/RegistryOps;of(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;)Lnet/minecraft/util/dynamic/RegistryOps;"))
	private static void createSaveProperties(LevelStorage.Session session, DynamicRegistryManager.Impl impl, ResourceManager resourceManager, DataPackSettings dataPackSettings, CallbackInfoReturnable<SaveProperties> cir) {
		Path saveDir = ((AccessorLevelStorageSession) session).getDirectory();
		PersistentDynamicRegistryHandler.remapDynamicRegistries(impl, saveDir);
	}

	// synthetic in method_29607 just after RegistryOps.of
	@Inject(method = "method_31125", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/dynamic/RegistryOps;of(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;)Lnet/minecraft/util/dynamic/RegistryOps;"))
	private static void method_31125(DynamicRegistryManager.Impl impl, GeneratorOptions generatorOptions, LevelInfo levelInfo, LevelStorage.Session session, DynamicRegistryManager.Impl impl2, ResourceManager resourceManager, DataPackSettings dataPackSettings, CallbackInfoReturnable<SaveProperties> cir) {
		Path saveDir = ((AccessorLevelStorageSession) session).getDirectory();
		PersistentDynamicRegistryHandler.remapDynamicRegistries(impl, saveDir);
	}
}
