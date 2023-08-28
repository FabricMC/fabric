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
import net.minecraft.util.Identifier;

/**
 * Fabric-provided extensions for {@link ServerConfigurationNetworkHandler}.
 * This interface is automatically implemented via Mixin and interface injection.
 */
public interface FabricServerConfigurationNetworkHandler {
	/**
	 * Enqueues a {@link ServerPlayerConfigurationTask} task to be processed.
	 *
	 * <p>Before adding a task use {@link ServerConfigurationNetworking#canSend(ServerConfigurationNetworkHandler, Identifier)}
	 * to ensure that the client can process this task.
	 *
	 * <p>Once the client has handled the task a packet should be sent to the server.
	 * Upon receiving this packet the server should call {@link FabricServerConfigurationNetworkHandler#completeTask(ServerPlayerConfigurationTask.Key)},
	 * otherwise the client cannot join the world.
	 *
	 * @param task the task
	 */
	default void addTask(ServerPlayerConfigurationTask task) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Completes the task identified by {@code key}.
	 *
	 * @param key the task key
	 * @throws IllegalStateException if the current task is not {@code key}
	 */
	default void completeTask(ServerPlayerConfigurationTask.Key key) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
