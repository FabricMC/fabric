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

package net.fabricmc.fabric.api.container;

import java.util.function.Consumer;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.impl.container.ContainerProviderImpl;

/**
 * @deprecated Use {@link net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry} instead.
 */
@Deprecated
public interface ContainerProviderRegistry {
	ContainerProviderRegistry INSTANCE = ContainerProviderImpl.INSTANCE;

	/**
	 * Register a "packet buffer -&gt; container" factory. This is used both on the client and server side.
	 *
	 * @param identifier a shared identifier, this identifier should also be used to register a container using {@link ScreenProviderRegistry}
	 * @param factory    the ContainerFactory that should return a new {@link ScreenHandler}
	 */
	void registerFactory(Identifier identifier, ContainerFactory<ScreenHandler> factory);

	/**
	 * Open a modded container.
	 *
	 * @param identifier the identifier that was used when registering the container
	 * @param player     the player that should open the container
	 * @param writer     a PacketByteBuf where data can be written to, this data is then accessible by the container factory when creating the container or the gui
	 */
	void openContainer(Identifier identifier, ServerPlayerEntity player, Consumer<PacketByteBuf> writer);

	/**
	 * Open a modded container. This should be called on the server side - it has no effect on the client side.
	 *
	 * @param identifier the identifier that was used when registering the container
	 * @param player     the player that should open the container
	 * @param writer     a PacketByteBuf where data can be written to, this data is then accessible by the container factory when creating the container or the gui
	 */
	void openContainer(Identifier identifier, PlayerEntity player, Consumer<PacketByteBuf> writer);
}
