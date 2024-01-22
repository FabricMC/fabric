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

package net.fabricmc.fabric.impl.screenhandler.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.impl.screenhandler.Networking;

public final class ClientNetworking implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-screen-handler-api-v1/client");

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(Networking.OpenScreenPayload.ID, (payload, context) -> {
			this.openScreen(payload);
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private <D> void openScreen(Networking.OpenScreenPayload<D> payload) {
		Identifier typeId = payload.identifier();
		int syncId = payload.syncId();
		Text title = payload.title();

		ScreenHandlerType<?> type = Registries.SCREEN_HANDLER.get(typeId);

		if (type == null || payload.data() == null) {
			LOGGER.warn("Unknown screen handler ID: {}", typeId);
			return;
		}

		if (!(type instanceof ExtendedScreenHandlerType)) {
			LOGGER.warn("Received extended opening packet for non-extended screen handler {}", typeId);
			return;
		}

		HandledScreens.Provider screenFactory = HandledScreens.getProvider(type);

		if (screenFactory != null) {
			MinecraftClient client = MinecraftClient.getInstance();
			PlayerEntity player = client.player;

			Screen screen = screenFactory.create(
					((ExtendedScreenHandlerType<?, D>) type).create(syncId, player.getInventory(), payload.data()),
					player.getInventory(),
					title
			);

			player.currentScreenHandler = ((ScreenHandlerProvider<?>) screen).getScreenHandler();
			client.setScreen(screen);
		} else {
			LOGGER.warn("Screen not registered for screen handler {}!", typeId);
		}
	}
}
