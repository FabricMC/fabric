/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.client.container;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.container.ContainerFactory;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides the client side helpers for ContainerHelper
 */
public class GuiHelper implements ClientModInitializer {

	private static final Identifier OPEN_CONTAINER = new Identifier("fabric", "open_container");
	private static final Map<Identifier, ContainerFactory<Gui>> FACTORIES = new HashMap<>();

	/**
	 * Register a gui handler
	 *
	 * @param identifier the id for the gui, must be the same as the container
	 * @param context a container context
	 */
	public static void register(Identifier identifier, ContainerFactory<Gui> context) {
		if (FACTORIES.containsKey(identifier)) {
			throw new RuntimeException("A factory has already been registered as " + identifier.toString());
		}
		FACTORIES.put(identifier, context);
	}

	@Override
	public void onInitializeClient() {
		CustomPayloadPacketRegistry.CLIENT.register(OPEN_CONTAINER, (packetContext, packetByteBuf) -> {
			Identifier identifier = new Identifier(packetByteBuf.readString(64));
			MinecraftClient.getInstance().execute(() -> {
				int syncId = packetByteBuf.readInt();
				Gui gui = FACTORIES.get(identifier).create(packetContext.getPlayer(), packetByteBuf);
				MinecraftClient.getInstance().openGui(gui);
				if (MinecraftClient.getInstance().currentGui instanceof ContainerGui) {
					((ContainerGui) MinecraftClient.getInstance().currentGui).container.syncId = syncId;
				}
			});
		});
	}
}
