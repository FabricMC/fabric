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

package net.fabricmc.fabric.impl.lifecycle.client;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.lifecycle.ServerLifecycleInternals;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public final class IntegratedServerLifecycleInternals extends ServerLifecycleInternals implements ClientModInitializer {
	public IntegratedServerLifecycleInternals() {
		// On an a client, the server is in the client
		super(() -> {
			// Get the client
			final MinecraftClient client = (MinecraftClient) FabricLoader.getInstance().getGameInstance();

			// And return the client's integrated server
			return client.getServer();
		});
	}

	@Override
	public void onInitializeClient() {
	}
}
