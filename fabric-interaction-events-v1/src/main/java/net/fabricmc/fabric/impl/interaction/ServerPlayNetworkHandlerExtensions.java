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

package net.fabricmc.fabric.impl.interaction;

import java.util.Objects;

import net.minecraft.server.network.ServerPlayNetworkHandler;

import net.fabricmc.fabric.api.interaction.v1.event.player.AirInteractionAccuracy;

public interface ServerPlayNetworkHandlerExtensions {
	static AirInteractionAccuracy getAccuracy(ServerPlayNetworkHandler handler) {
		Objects.requireNonNull(handler, "Network handler cannot be null!");

		return ((ServerPlayNetworkHandlerExtensions) handler).fabric_getInteractionAccuracy();
	}

	AirInteractionAccuracy fabric_getInteractionAccuracy();

	void fabric_setInteractionAccuracy(AirInteractionAccuracy accuracy);

	/**
	 * @return whether the packet has been sent notifying the client that the event has definitely occurred.
	 */
	boolean fabric_handledDefiniteEvent();

	/**
	 * Resets the tracking state for definite event tracking.
	 */
	void fabric_resetEventHandling();
}
