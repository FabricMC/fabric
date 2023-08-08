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

package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerConfigurationTask;

/**
 * Fabric-provided extensions for {@link ServerConfigurationNetworkHandler}.
 * This interface is automatically implemented via Mixin and interface injection.
 */
public interface FabricServerConfigurationNetworkHandler {
	/**
	 * Enqueue a {@link ServerPlayerConfigurationTask} task to be processed.
	 *
	 * @param task
	 */
	default void addTask(ServerPlayerConfigurationTask task) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 *
	 * @param key
	 */
	default void completeTask(ServerPlayerConfigurationTask.Key key) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
