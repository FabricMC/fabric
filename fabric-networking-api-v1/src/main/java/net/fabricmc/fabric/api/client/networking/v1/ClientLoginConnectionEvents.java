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

package net.fabricmc.fabric.api.client.networking.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientLoginConnectionEvents {
	/**
	 * An event for when a client's login process has begun.
	 * This event may be used by mods to prepare their client side state.
	 * This event does not guarantee that a login attempt will be successful.
	 *
	 * <p>No packets should be sent when this event is invoked.
	 */
	public static final Event<LoginInit> LOGIN_INIT = EventFactory.createArrayBacked(LoginInit.class, callbacks -> (handler, client) -> {
		for (LoginInit callback : callbacks) {
			callback.onLoginStart(handler, client);
		}
	});

	/**
	 * An event for when a client has started receiving login queries.
	 * A client can only start receiving login queries when a server has sent the first login query.
	 * Vanilla servers will typically never make the client enter this login phase.
	 * If this event is fired then it is a sign that a server is not a vanilla server.
	 *
	 * <p>This event may be used to register login query handlers which may be used to send a response to a server.
	 *
	 * <p>No packets should be sent when this event is invoked.
	 */
	public static final Event<LoginQueryStart> LOGIN_QUERY_START = EventFactory.createArrayBacked(LoginQueryStart.class, callbacks -> (handler, client) -> {
		for (LoginQueryStart callback : callbacks) {
			callback.onLoginQueryStart(handler, client);
		}
	});

	/**
	 * An event for when a client's login process has ended due to disconnection.
	 *
	 * <p>No packets should be sent when this event is invoked.
	 */
	public static final Event<LoginDisconnect> LOGIN_DISCONNECT = EventFactory.createArrayBacked(LoginDisconnect.class, callbacks -> (handler, client) -> {
		for (LoginDisconnect callback : callbacks) {
			callback.onLoginDisconnect(handler, client);
		}
	});

	private ClientLoginConnectionEvents() {
	}

	@FunctionalInterface
	public interface LoginInit {
		void onLoginStart(ClientLoginNetworkHandler handler, MinecraftClient client);
	}

	@FunctionalInterface
	public interface LoginQueryStart {
		void onLoginQueryStart(ClientLoginNetworkHandler handler, MinecraftClient client);
	}

	@FunctionalInterface
	public interface LoginDisconnect {
		void onLoginDisconnect(ClientLoginNetworkHandler handler, MinecraftClient client);
	}
}
