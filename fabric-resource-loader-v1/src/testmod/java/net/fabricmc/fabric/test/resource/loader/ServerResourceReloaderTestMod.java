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

package net.fabricmc.fabric.test.resource.loader;

import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.loader.v1.ServerResourceReloadEvents;

public class ServerResourceReloaderTestMod implements ModInitializer {
	public static final String MODID = "fabric-resource-loader-v1-testmod";

	private static boolean serverResources = false;
	private static int registerReloadersCount = 0;
	private static int startReloadCount = 0;
	private static int endReloadCount = 0;

	@Override
	public void onInitialize() {
		setupServerReloadListeners();

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			if (!serverResources) {
				throw new AssertionError("Server reload listener was not called.");
			}
		});
	}

	private void setupServerReloadListeners() {
		ServerResourceReloadEvents.REGISTER_RELOADERS.register(context -> {
			context.addReloader(new Identifier(MODID, "server_second"), (SynchronousResourceReloader) manager -> {
				if (!serverResources) {
					throw new AssertionError("Second reload listener was called before the first!");
				}
			});
			context.addReloader(new Identifier(MODID, "server_first"), (SynchronousResourceReloader) manager -> {
				serverResources = true;
			});
			context.addReloaderOrdering(new Identifier(MODID, "server_first"), new Identifier(MODID, "server_second"));

			if (registerReloadersCount != startReloadCount || registerReloadersCount != endReloadCount) {
				throw new AssertionError("Inconsistent server reload event count: (%d, %d, %d)".formatted(registerReloadersCount, startReloadCount, endReloadCount));
			}

			registerReloadersCount++;
		});

		ServerResourceReloadEvents.START_RELOAD.register(context -> {
			startReloadCount++;

			if (startReloadCount != registerReloadersCount) {
				throw new AssertionError("Start reload is %s, expected %d".formatted(startReloadCount, registerReloadersCount));
			}
		});

		ServerResourceReloadEvents.END_RELOAD.register((context, success) -> {
			endReloadCount++;

			if (endReloadCount != startReloadCount) {
				throw new AssertionError("Start reload is %s, expected %d".formatted(startReloadCount, registerReloadersCount));
			}
		});
	}
}
