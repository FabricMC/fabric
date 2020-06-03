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

package net.fabricmc.fabric.impl.lifecycle;

import java.util.function.Supplier;

import net.minecraft.server.MinecraftServer;

public abstract class ServerLifecycleInternals {
	private static Supplier<MinecraftServer> SERVER_SUPPLIER;

	/* @Nullable */
	public static MinecraftServer getServer() {
		return SERVER_SUPPLIER.get();
	}

	protected ServerLifecycleInternals(Supplier<MinecraftServer> supplier) {
		SERVER_SUPPLIER = supplier;
	}
}
