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

import net.fabricmc.fabric.api.networking.v1.ServerNetworking;
import net.fabricmc.fabric.impl.networking.SimpleChannelHandlerRegistry;

public final class ServerNetworkingDetails {
	public static final SimpleChannelHandlerRegistry<ServerNetworking.LoginChannelHandler> LOGIN = new SimpleChannelHandlerRegistry<>();
	public static final SimpleChannelHandlerRegistry<ServerNetworking.PlayChannelHandler> PLAY = new SimpleChannelHandlerRegistry<>();

	public static ServerPlayNetworkAddon getAddon(ServerPlayNetworkHandler handler) {
		return ((ServerPlayNetworkHandlerHook) handler).getAddon();
	}
}
