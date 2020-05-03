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

package net.fabricmc.fabric.impl.networking.client;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.fabricmc.fabric.api.networking.v1.client.ClientLoginContext;
import net.fabricmc.fabric.api.networking.v1.client.ClientPlayContext;
import net.fabricmc.fabric.impl.networking.BasicPacketReceiver;

public final class ClientNetworkingDetails {
	public static final BasicPacketReceiver<ClientLoginContext> LOGIN = new BasicPacketReceiver<>();
	public static final BasicPacketReceiver<ClientPlayContext> PLAY = new BasicPacketReceiver<>();

	public static ClientPlayNetworkAddon getAddon(ClientPlayNetworkHandler handler) {
		return ((ClientPlayNetworkHandlerHook) handler).getAddon();
	}

	public static ClientLoginNetworkAddon getAddon(ClientLoginNetworkHandler handler) {
		return ((ClientLoginNetworkHandlerHook) handler).getAddon();
	}
}
