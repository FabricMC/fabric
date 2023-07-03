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

package net.fabricmc.fabric.api.server.consent.v1.client;

import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 */
@ApiStatus.Experimental
public interface ClientFabricServerConsentFlagsCallback {
	/**
	 * An event to notify the client that the server has sent their list of illegal flags.
	 */
	Event<ClientFabricServerConsentFlagsCallback> FLAGS_SENT = EventFactory.createArrayBacked(ClientFabricServerConsentFlagsCallback.class, callbacks -> (client, handler, flags) -> {
		for (ClientFabricServerConsentFlagsCallback callback : callbacks) {
			callback.onFlagsSent(client, handler, flags);
		}
	});

	void onFlagsSent(MinecraftClient client, ClientPlayNetworkHandler handler, List<Identifier> flags);
}
