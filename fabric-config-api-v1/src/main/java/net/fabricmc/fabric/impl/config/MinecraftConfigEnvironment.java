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

package net.fabricmc.fabric.impl.config;

import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.loader.api.config.SaveType;
import net.fabricmc.loader.api.config.entrypoint.ConfigEnvironment;
import net.fabricmc.loader.api.config.entrypoint.ConfigPostInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.config.value.ValueContainerProvider;

public class MinecraftConfigEnvironment implements ConfigPostInitializer, ConfigEnvironment {
	@Override
	public void onConfigsLoaded() {
		ValueContainerProvider.register(saveType -> {
			EnvType envType = FabricLoader.getInstance().getEnvironmentType();

			if (saveType == FabricSaveTypes.LEVEL && envType == EnvType.CLIENT) {
				MinecraftClient client = MinecraftClient.getInstance();

				if (client.isIntegratedServerRunning() && client.getServer() != null) {
					return ((ValueContainerProvider) client.getServer());
				} else if (client.getCurrentServerEntry() != null) {
					return ((ValueContainerProvider) client.getCurrentServerEntry());
				}
			} else if (saveType == FabricSaveTypes.USER && envType == EnvType.SERVER) {
				return ((ValueContainerProvider) FabricLoader.getInstance().getGameInstance());
			} else if (saveType == FabricSaveTypes.USER && envType == EnvType.CLIENT) {
				return ((ValueContainerProvider) MinecraftClient.getInstance().getCurrentServerEntry());
			}

			return null;
		});
	}

	@Override
	public void addToRoot(Consumer<SaveType> consumer) {
		switch (FabricLoader.getInstance().getEnvironmentType()) {
		case CLIENT:
			consumer.accept(FabricSaveTypes.USER);
		case SERVER:
			consumer.accept(FabricSaveTypes.LEVEL);
		}
	}
}
