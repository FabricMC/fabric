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

package net.fabricmc.fabric.impl.server.consent.client;

import static net.fabricmc.fabric.impl.server.consent.FabricServerConsentImpl.FLAGS_CHANNEL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.server.consent.v1.client.ClientFabricServerConsentFlagsCallback;
import net.fabricmc.fabric.impl.server.consent.FabricServerConsentImpl;

public final class ClientFabricServerConsentImpl implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(FabricServerConsentImpl.class);

	public static List<Identifier> illegalFlags = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(FLAGS_CHANNEL, (client, handler, buf, responseSender) -> {
			illegalFlags = buf.readCollection(ArrayList::new, PacketByteBuf::readIdentifier);
			client.execute(() -> {
				try {
					ClientFabricServerConsentFlagsCallback.FLAGS_SENT.invoker().onFlagsSent(client, handler, Collections.unmodifiableList(illegalFlags));
				} catch (RuntimeException e) {
					LOGGER.error("Exception thrown while invoking ClientFabricServerConsentFlagsCallback.FLAGS_SENT", e);
				}
			});
		});
	}
}
