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

package net.fabricmc.fabric.api.networking.v1.event;

import net.minecraft.network.listener.PacketListener;

/**
 * The callback for a state change in a network handler.
 *
 * <p>For play network handler, all state change events are fired on the game engine thread.
 *
 * @param <T> the network handler type
 */
@FunctionalInterface
public interface NetworkHandlerCallback<T extends PacketListener> {
	/**
	 * Handles the state event of the network handler.
	 *
	 * @param handler the network handler
	 */
	void handle(T handler);
}
