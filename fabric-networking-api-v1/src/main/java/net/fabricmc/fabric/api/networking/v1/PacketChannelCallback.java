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

import java.util.List;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.client.ClientNetworking;
import net.fabricmc.fabric.api.networking.v1.server.ServerNetworking;

/**
 * A callback that involves a network handler and a list of channels.
 *
 * @param <L> the network handler
 * @see ServerNetworking#CHANNEL_REGISTERED
 * @see ServerNetworking#CHANNEL_UNREGISTERED
 * @see ClientNetworking#CHANNEL_REGISTERED
 * @see ClientNetworking#CHANNEL_UNREGISTERED
 */
@FunctionalInterface
public interface PacketChannelCallback<L extends PacketListener> {
	/**
	 * Receive the network handler and the channels.
	 *
	 * @param handler  the network handler
	 * @param channels the channels
	 */
	void handle(L handler, List<Identifier> channels);
}
