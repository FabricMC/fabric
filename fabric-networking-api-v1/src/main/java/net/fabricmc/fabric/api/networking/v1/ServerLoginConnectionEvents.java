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

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerLoginConnectionEvents {
	/**
	 * An event for the start of login queries of the server login network handler.
	 */
	public static final Event<LoginQueryStart> LOGIN_QUERY_START = EventFactory.createArrayBacked(LoginQueryStart.class, callbacks -> (handler, server, sender, synchronizer) -> {
		for (LoginQueryStart callback : callbacks) {
			callback.onLoginStart(handler, server, sender, synchronizer);
		}
	});
	/**
	 * An event for the disconnection of the server login network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.
	 */
	public static final Event<LoginDisconnect> LOGIN_DISCONNECT = EventFactory.createArrayBacked(LoginDisconnect.class, callbacks -> (handler, server) -> {
		for (LoginDisconnect callback : callbacks) {
			callback.onLoginDisconnect(handler, server);
		}
	});

	private ServerLoginConnectionEvents() {
	}

	@FunctionalInterface
	public interface LoginQueryStart {
		void onLoginStart(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer);
	}

	@FunctionalInterface
	public interface LoginDisconnect {
		void onLoginDisconnect(ServerLoginNetworkHandler handler, MinecraftServer server);
	}
}
