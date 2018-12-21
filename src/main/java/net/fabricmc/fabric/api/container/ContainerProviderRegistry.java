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

package net.fabricmc.fabric.api.container;

import net.fabricmc.fabric.api.client.gui.GuiProviderRegistry;
import net.fabricmc.fabric.impl.container.ContainerProviderImpl;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.function.Consumer;

public interface ContainerProviderRegistry {

	ContainerProviderRegistry INSTANCE = ContainerProviderImpl.INSTANCE;

	/**
	 *
	 * Register a container factory
	 *
	 * @param identifier a shared identifier, this identifier should also be used to register a container using {@link GuiProviderRegistry}
	 * @param factory the ContainerFactory that should return a new {@link Container}
	 */
	void registerFactory(Identifier identifier, ContainerFactory<Container> factory);

	/**
	 *
	 * This is used to open a container on the client, call this from the server
	 *
	 * @param identifier the identifier that was used when registering the container
	 * @param player the player that should open the container
	 * @param writer a PacketByteBuf where data can be written to, this data is then accessible by the container factory when creating the container or the gui
	 */
	void openContainer(Identifier identifier, ServerPlayerEntity player, Consumer<PacketByteBuf> writer);

	/**
	 *
	 * This is used to create a new container from the registered factory's
	 *
	 * @param identifier the identifier that was registered when creating the container factory
	 * @param player the player that the container is being opened on
	 * @param buf the PacketByteBuf that can contain data supplied when opening the container
	 * @return a new container that has been created with the registered factories
	 */
	<C extends Container> C createContainer(Identifier identifier, PlayerEntity player, PacketByteBuf buf);
}
