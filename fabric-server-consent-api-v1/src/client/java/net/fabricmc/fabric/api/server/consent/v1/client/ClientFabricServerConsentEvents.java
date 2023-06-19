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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

public final class ClientFabricServerConsentEvents {
	/**
	 * An event to notify the client that the server has sent their list of illegal mods.
	 */
	public static final Event<ModsSent> MODS_SENT = EventFactory.createArrayBacked(ModsSent.class, callbacks -> (client, handler, buf, responseSender) -> {
		for (ModsSent callback : callbacks) {
			callback.onModsSent(client, handler, buf, responseSender);
		}
	});

	/**
	 * An event to notify the client that the server has sent their list of illegal features.
	 */
	public static final Event<FeaturesSent> FEATURES_SENT = EventFactory.createArrayBacked(FeaturesSent.class, callbacks -> (client, handler, buf, responseSender) -> {
		for (FeaturesSent callback : callbacks) {
			callback.onFeaturesSent(client, handler, buf, responseSender);
		}
	});

	@FunctionalInterface
	public interface ModsSent {
		void onModsSent(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
	}

	@FunctionalInterface
	public interface FeaturesSent {
		void onFeaturesSent(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
	}
}
