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

package net.fabricmc.fabric.mixin.gametest.server;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
import net.minecraft.world.level.storage.LevelSummary;

import net.fabricmc.fabric.impl.gametest.FabricGameTestHelper;

@Mixin(Main.class)
public class MainMixin {
	@Redirect(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/EulaReader;isEulaAgreedTo()Z"))
	private static boolean isEulaAgreedTo(EulaReader reader) {
		return FabricGameTestHelper.ENABLED || reader.isEulaAgreedTo();
	}

	@Inject(method = "main", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage$Session;readLevelProperties(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resource/DataPackSettings;)Lnet/minecraft/world/SaveProperties;"))
	private static void main(String[] args, CallbackInfo info, OptionParser optionParser, OptionSpec o1, OptionSpec o2, OptionSpec o3, OptionSpec o4, OptionSpec o5, OptionSpec o6, OptionSpec o7, OptionSpec o8, OptionSpec o9, OptionSpec o10, OptionSpec o11, OptionSpec o12, OptionSpec o13, OptionSpec o14, OptionSet optionSet, DynamicRegistryManager.Impl impl, Path path, ServerPropertiesLoader serverPropertiesLoader, Path path2, EulaReader eulaReader, File file, YggdrasilAuthenticationService yggdrasilAuthenticationService, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, String string, LevelStorage levelStorage, LevelStorage.Session session, LevelSummary levelSummary, DataPackSettings dataPackSettings, boolean bl, ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings2, CompletableFuture completableFuture, ServerResourceManager serverResourceManager, RegistryOps registryOps) {
		if (FabricGameTestHelper.ENABLED) {
			FabricGameTestHelper.runHeadlessServer(session, resourcePackManager, serverResourceManager, impl);
			info.cancel();  // Do not progress in starting the normal dedicated server
		}
	}

	// Exit with a non-zero exit code when the server fails to start.
	// Otherwise gradlew test will succeed without errors, although no tests have been run.
	@Inject(method = "main", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;fatal(Ljava/lang/String;Ljava/lang/Throwable;)V", shift = At.Shift.AFTER))
	private static void exitOnError(CallbackInfo info) {
		if (FabricGameTestHelper.ENABLED) {
			System.exit(-1);
		}
	}
}
