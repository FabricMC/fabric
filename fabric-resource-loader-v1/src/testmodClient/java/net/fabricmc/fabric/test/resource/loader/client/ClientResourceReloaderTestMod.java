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

package net.fabricmc.fabric.test.resource.loader.client;

import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.loader.v1.client.ClientResourceReloadEvents;

public class ClientResourceReloaderTestMod implements ClientModInitializer {
	public static final String MODID = "fabric-resource-loader-v1-testmod";

	private static boolean clientResources = false;
	private static int registerReloadersCount = 0;
	private static int startReloadCount = 0;
	private static int endReloadCount = 0;

	@Override
	public void onInitializeClient() {
		setupClientReloadListeners();

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			if (!clientResources) {
				throw new AssertionError("Client reload listener was not called.");
			}
		});
	}

	private void setupClientReloadListeners() {
		ClientResourceReloadEvents.REGISTER_RELOADERS.register(context -> {
			context.addReloader(new Identifier(MODID, "client_second"), (SynchronousResourceReloader) manager -> {
				if (!clientResources) {
					throw new AssertionError("Second reload listener was called before the first!");
				}
			});
			context.addReloader(new Identifier(MODID, "client_first"), (SynchronousResourceReloader) manager -> {
				clientResources = true;
			});
			context.addReloaderOrdering(new Identifier(MODID, "client_first"), new Identifier(MODID, "client_second"));

			if (registerReloadersCount != 0) {
				throw new AssertionError("Should only gather client reload listeners once");
			}

			registerReloadersCount++;
		});

		ClientResourceReloadEvents.START_RELOAD.register(context -> {
			if (startReloadCount != endReloadCount) {
				throw new AssertionError("Start reload is %s, expected %d".formatted(startReloadCount, endReloadCount));
			}

			startReloadCount++;
		});

		ClientResourceReloadEvents.END_RELOAD.register((context, success) -> {
			endReloadCount++;

			if (startReloadCount != endReloadCount) {
				throw new AssertionError("Start reload is %s, expected %d".formatted(startReloadCount, endReloadCount));
			}
		});
	}
}
