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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.fabricmc.fabric.impl.screenhandler.Networking;

@Environment(EnvType.CLIENT)
public final class ClientNetworking implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("fabric-screen-handler-api-v1/client");

	@Override
	public void onInitializeClient() {
		ClientSidePacketRegistry.INSTANCE.register(Networking.OPEN_ID, (ctx, buf) -> {
			Identifier typeId = buf.readIdentifier();
			int syncId = buf.readVarInt();
			Text title = buf.readText();
			buf.retain();
			ctx.getTaskQueue().execute(() -> openScreen(typeId, syncId, title, buf));
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void openScreen(Identifier typeId, int syncId, Text title, PacketByteBuf buf) {
		ScreenHandlerType<?> type = Registry.SCREEN_HANDLER.get(typeId);

		if (type == null) {
			LOGGER.warn("Unknown screen handler ID: {}", typeId);
			return;
		}

		if (!(type instanceof ExtendedScreenHandlerType<?>)) {
			LOGGER.warn("Received extended opening packet for non-extended screen handler {}", typeId);
			return;
		}

		HandledScreens.Provider screenFactory = HandledScreens.getProvider(type);

		if (screenFactory != null) {
			MinecraftClient client = MinecraftClient.getInstance();
			PlayerEntity player = client.player;

			Screen screen = screenFactory.create(
					((ExtendedScreenHandlerType<?>) type).create(syncId, player.inventory, buf),
					player.inventory,
					title
			);

			player.currentScreenHandler = ((ScreenHandlerProvider<?>) screen).getScreenHandler();
			client.openScreen(screen);
		} else {
			LOGGER.warn("Screen not registered for screen handler {}!", typeId);
		}
	}
}
