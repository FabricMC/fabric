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

import net.minecraft.network.listener.PacketListener;

import net.fabricmc.fabric.api.networking.v1.client.ClientNetworking;
import net.fabricmc.fabric.api.networking.v1.server.ServerNetworking;

/**
 * A callback that involves a network handler.
 *
 * @param <L> the network handler
 * @see ServerNetworking#PLAY_INITIALIZED
 * @see ServerNetworking#PLAY_DISCONNECTED
 * @see ServerNetworking#LOGIN_QUERY_START
 * @see ServerNetworking#LOGIN_DISCONNECTED
 * @see ClientNetworking#PLAY_INITIALIZED
 * @see ClientNetworking#PLAY_DISCONNECTED
 */
@FunctionalInterface
public interface PacketListenerCallback<L extends PacketListener> {
	/**
	 * Receive the network handler.
	 *
	 * @param handler the network handler
	 */
	void handle(L handler);
}
