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

package net.fabricmc.fabric.test.server.consent.client;

import com.mojang.brigadier.Command;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.server.consent.v1.client.ClientFabricServerConsent;
import net.fabricmc.fabric.api.server.consent.v1.client.ClientFabricServerConsentFlagsCallback;
import net.fabricmc.fabric.impl.server.consent.client.ClientFabricServerConsentImpl;

public class ClientServerConsentTest implements ClientModInitializer {
	public static final String MOD_ID = "fabric-server-consent-api-v1-testmod";

	private static final Identifier EXAMPLE_FLAG = Identifier.of(MOD_ID, "example_feature");
	private static final Identifier EXAMPLE_FLAG_2 = Identifier.of(MOD_ID, "example_feature_2");

	@Override
	public void onInitializeClient() {
		ClientFabricServerConsentFlagsCallback.FLAGS_SENT.register((context, flags) -> {
			flags.forEach(flag -> ClientFabricServerConsentImpl.LOGGER.info("Illegal flag: {}", flag));
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal(EXAMPLE_FLAG.toString())
					.requires(source -> {
						if (ClientFabricServerConsent.isIllegal(EXAMPLE_FLAG)) {
							source.sendError(Text.of("Flag disabled: " + EXAMPLE_FLAG));
							return false;
						}

						return true;
					})
					.executes(context -> {
						context.getSource().sendFeedback(Text.of("Legal flag: " + EXAMPLE_FLAG));
						return Command.SINGLE_SUCCESS;
					}));

			dispatcher.register(ClientCommandManager.literal(EXAMPLE_FLAG_2.toString())
					.requires(source -> {
						if (ClientFabricServerConsent.isIllegal(EXAMPLE_FLAG_2)) {
							source.sendError(Text.of("Flag disabled: " + EXAMPLE_FLAG_2));
							return false;
						}

						return true;
					})
					.executes(context -> {
						context.getSource().sendFeedback(Text.of("Legal flag: " + EXAMPLE_FLAG_2));
						return Command.SINGLE_SUCCESS;
					}));
		});
	}
}
