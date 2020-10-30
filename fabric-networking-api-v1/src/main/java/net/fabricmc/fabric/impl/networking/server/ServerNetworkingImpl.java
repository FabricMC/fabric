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

package net.fabricmc.fabric.impl.networking.server;

import net.minecraft.server.network.ServerPlayNetworkHandler;

import net.fabricmc.fabric.api.networking.v1.login.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.play.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.ChannelRegistry;

public final class ServerNetworkingImpl {
	public static final ChannelRegistry<ServerLoginNetworking.LoginChannelHandler> LOGIN = new ChannelRegistry<>();
	public static final ChannelRegistry<ServerPlayNetworking.PlayChannelHandler> PLAY = new ChannelRegistry<>();

	public static ServerPlayNetworkAddon getAddon(ServerPlayNetworkHandler handler) {
		return ((ServerPlayNetworkHandlerHook) handler).getAddon();
	}
}
