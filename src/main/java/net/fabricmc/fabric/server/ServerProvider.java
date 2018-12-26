/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.server;

import net.minecraft.server.MinecraftServer;

/**
 * Allows retrieval of the MinecraftServer instance.
 */
public final class ServerProvider {
	private static MinecraftServer instance = null;

	/**
	 * Returns the current {@code MinecraftServer} instance.
	 * 
	 * @return On (physical) client-side, returns an instance of {@code IntegratedServer} when in a
	 *         world, returns null otherwise. On (physical) server-side returns an instance of
	 *         {@code DedicatedServer}
	 */
	public static MinecraftServer get() {
		return ServerProvider.instance;
	}

	/**
	 * This method is not meant to be called directly.
	 * 
	 * @param server
	 */
	public static void set(MinecraftServer server) {
		if (server == null || instance == null)
			ServerProvider.instance = server;
	}
}
