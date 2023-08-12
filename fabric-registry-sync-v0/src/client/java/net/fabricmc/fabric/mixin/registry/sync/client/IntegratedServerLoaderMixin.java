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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.impl.client.registry.sync.RegistryRemovalChecker;
import net.fabricmc.fabric.impl.client.registry.sync.RemovedRegistryEntryWarningScreen;

@Mixin(IntegratedServerLoader.class)
public abstract class IntegratedServerLoaderMixin {
	@Shadow
	private static void close(LevelStorage.Session session, String levelName) {
	}

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Final
	private LevelStorage storage;

	@Shadow
	protected abstract void start(Screen parent, String levelName, boolean safeMode, boolean canShowBackupPrompt);

	@Inject(method = "start(Lnet/minecraft/client/gui/screen/Screen;Ljava/lang/String;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/SaveLoader;saveProperties()Lnet/minecraft/world/SaveProperties;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void doRegistryRemovalCheck(Screen parent, String levelName, boolean safeMode, boolean canShowBackupPrompt, CallbackInfo ci, LevelStorage.Session session, ResourcePackManager unused, SaveLoader saveLoader) {
		if (!canShowBackupPrompt) return;

		Path jsonFile = session.getDirectory(WorldSavePath.ROOT).resolve(RegistryRemovalChecker.FILE_NAME);
		RegistryRemovalChecker checker = RegistryRemovalChecker.runCheck(jsonFile);

		if (checker == null || checker.getMissingNamespaces().isEmpty()) {
			RegistryRemovalChecker.write(jsonFile);
			return;
		}

		RegistryRemovalChecker.LOGGER.warn("Registry removal check failed! This often occurs when you update or remove a mod.");
		RegistryRemovalChecker.LOGGER.info("Missing registry entry namespaces (usually the same as mod ID): {}", checker.getMissingNamespaces());
		RegistryRemovalChecker.LOGGER.info("Missing registry entries: {}", checker.getMissingKeys());
		RegistryRemovalChecker.LOGGER.info("Missing block states: {}", checker.getMissingBlockStates());

		this.client.setScreen(new RemovedRegistryEntryWarningScreen(parent, (backup, eraseCache) -> {
			if (backup) {
				EditWorldScreen.onBackupConfirm(this.storage, levelName);
				this.start(parent, levelName, safeMode, false);
			}
		}, checker.getMissingNamespaces()));
		saveLoader.close();
		close(session, levelName);
		ci.cancel();
	}
}
