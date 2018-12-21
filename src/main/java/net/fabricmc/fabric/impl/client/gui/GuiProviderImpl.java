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

package net.fabricmc.fabric.impl.client.gui;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.gui.GuiProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.container.GuiSupplier;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.container.Container;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GuiProviderImpl implements ClientModInitializer, GuiProviderRegistry {

	/**
	 * Use the instance provided by GuiProviderRegistry
	 */
	public static final GuiProviderRegistry INSTANCE = new GuiProviderImpl();

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Identifier OPEN_CONTAINER = new Identifier("fabric", "open_container");
	private static final Map<Identifier, ContainerFactory<ContainerGui>> FACTORIES = new HashMap<>();

	public void registerFactory(Identifier identifier, ContainerFactory<ContainerGui> factory) {
		if (FACTORIES.containsKey(identifier)) {
			throw new RuntimeException("A factory has already been registered as " + identifier.toString());
		}
		FACTORIES.put(identifier, factory);
	}

	@Override
	public <C extends Container> void registerFactory(Identifier identifier, GuiSupplier<C> guiSupplier) {
		registerFactory(identifier, (id, player, buf) -> guiSupplier.create(ContainerProviderRegistry.INSTANCE.createContainer(id, player, buf)));
	}

	@Override
	public void onInitializeClient() {
		CustomPayloadPacketRegistry.CLIENT.register(OPEN_CONTAINER, (packetContext, packetByteBuf) -> {
			Identifier identifier = packetByteBuf.readIdentifier();
			int syncId = packetByteBuf.readUnsignedByte();
			MinecraftClient.getInstance().execute(() -> {
				ContainerFactory<ContainerGui> factory = FACTORIES.get(identifier);
				if (factory == null) {
					LOGGER.error("No factory found for " + identifier.toString());
					return;
				}
				ContainerGui gui = factory.create(identifier, packetContext.getPlayer(), packetByteBuf);
				gui.container.syncId = syncId;
				MinecraftClient.getInstance().openGui(gui);
			});
		});
	}
}
