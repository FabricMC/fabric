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

package net.fabricmc.fabric.impl.container;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ContainerProviderImpl implements ContainerProviderRegistry {

	/**
	 * Use the instance provided by ContainerProviderRegistry
	 */
	public static final ContainerProviderImpl INSTANCE = new ContainerProviderImpl();

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Identifier OPEN_CONTAINER = new Identifier("fabric", "open_container");
	private static final Map<Identifier, ContainerFactory<Container>> FACTORIES = new HashMap<>();

	@Override
	public void registerFactory(Identifier identifier, ContainerFactory<Container> factory) {
		if (FACTORIES.containsKey(identifier)) {
			throw new RuntimeException("A factory has already been registered as " + identifier.toString());
		}
		FACTORIES.put(identifier, factory);
	}

	@Override
	public void openContainer(Identifier identifier, PlayerEntity player, Consumer<PacketByteBuf> writer) {
		if (!(player instanceof ServerPlayerEntity)) {
			LOGGER.warn("Please only use ContainerProviderRegistry.openContainer() with server-sided player entities!");
			return;
		}

		openContainer(identifier, (ServerPlayerEntity) player, writer);
	}

	@Override
	public void openContainer(Identifier identifier, ServerPlayerEntity player, Consumer<PacketByteBuf> writer) {
		SyncIdProvider syncIDProvider = (SyncIdProvider) player;
		int syncId = syncIDProvider.fabric_incrementSyncId();
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(identifier);
		buf.writeByte(syncId);

		writer.accept(buf);
		player.networkHandler.sendPacket(new CustomPayloadClientPacket(OPEN_CONTAINER, buf));

		PacketByteBuf clonedBuf = new PacketByteBuf(buf.duplicate());
		clonedBuf.readIdentifier();
		clonedBuf.readUnsignedByte();

		Container container = createContainer(syncId, identifier, player, clonedBuf);
		if(container == null){
			return;
		}
		player.container = container;
		player.container.addListener(player);
	}

	public <C extends Container> C createContainer(int syncId, Identifier identifier, PlayerEntity player, PacketByteBuf buf){
		ContainerFactory<Container> factory = FACTORIES.get(identifier);
		if (factory == null) {
			LOGGER.error("No container factory found for %s!", identifier.toString());
			return null;
		}
		return (C) factory.create(syncId, identifier, player, buf);
	}
}
