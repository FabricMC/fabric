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

import static net.fabricmc.fabric.impl.server.consent.FabricServerConsentImpl.FEATURES_CHANNEL;
import static net.fabricmc.fabric.impl.server.consent.FabricServerConsentImpl.MODS_CHANNEL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.server.consent.v1.client.ClientFabricServerConsentEvents;
import net.fabricmc.fabric.impl.server.consent.FabricServerConsentImpl;

public final class ClientFabricServerConsentImpl implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(FabricServerConsentImpl.class);

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(MODS_CHANNEL, (client, handler, buf, responseSender) -> {
			try {
				ClientFabricServerConsentEvents.MODS_SENT.invoker().onModsSent(client, handler, buf, responseSender);
			} catch (RuntimeException e) {
				LOGGER.error("Exception thrown while invoking ClientFabricServerConsent.MODS_SENT", e);
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(FEATURES_CHANNEL, (client, handler, buf, responseSender) -> {
			try {
				ClientFabricServerConsentEvents.FEATURES_SENT.invoker().onFeaturesSent(client, handler, buf, responseSender);
			} catch (RuntimeException e) {
				LOGGER.error("Exception thrown while invoking ClientFabricServerConsent.FEATURES_SENT", e);
			}
		});
	}
}
