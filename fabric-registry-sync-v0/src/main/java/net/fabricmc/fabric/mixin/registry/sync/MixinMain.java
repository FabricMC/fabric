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

package net.fabricmc.fabric.mixin.registry.sync;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.Main;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.util.UserCache;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.impl.registry.sync.PersistentDynamicRegistryHandler;

@Mixin(Main.class)
public class MixinMain {

	// just after RegistryOps.of
	@SuppressWarnings("rawtypes")
	@Inject(method = "main", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/dynamic/RegistryOps;of(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;)Lnet/minecraft/util/dynamic/RegistryOps;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void main(String[] args, CallbackInfo ci, OptionParser optionParser, OptionSpec optionSpec, OptionSpec optionSpec2, OptionSpec optionSpec3, OptionSpec optionSpec4, OptionSpec optionSpec5, OptionSpec optionSpec6, OptionSpec optionSpec7, OptionSpec optionSpec8, OptionSpec optionSpec9, OptionSpec optionSpec10, OptionSpec optionSpec11, OptionSpec optionSpec12, OptionSpec optionSpec13, OptionSpec optionSpec14, OptionSet optionSet, DynamicRegistryManager.Impl impl, Path path, ServerPropertiesLoader serverPropertiesLoader, Path path2, EulaReader eulaReader, File file, YggdrasilAuthenticationService yggdrasilAuthenticationService, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, String string, LevelStorage levelStorage, LevelStorage.Session session, DataPackSettings dataPackSettings, boolean bl, ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings2, CompletableFuture completableFuture, ServerResourceManager serverResourceManager2, RegistryOps registryOps) {
		Path saveDir = ((AccessorLevelStorageSession) session).getDirectory();
		PersistentDynamicRegistryHandler.remapDynamicRegistries(impl, saveDir);
	}
}
