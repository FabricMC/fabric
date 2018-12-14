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

package net.fabricmc.fabric.container;

import io.netty.buffer.Unpooled;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.container.Container;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Helper/registry for handling custom containers. This class is used to register the container and send the packet to the client
 */
public class ContainerHelper {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Identifier OPEN_CONTAINER = new Identifier("fabric", "open_container");
	private static final Map<Identifier, ContainerFactory<Container>> FACTORIES = new HashMap<>();

	public static void registerFactory(Identifier identifier, ContainerFactory<Container> context) {
		if (FACTORIES.containsKey(identifier)) {
			throw new RuntimeException("A factory has already been registered as " + identifier.toString());
		}
		FACTORIES.put(identifier, context);
	}

	/**
	 * Sends a pack to the client to open the gui, and opens the container on the server side
	 *
	 * @param identifier the identifier that you registered your gui and container handler with
	 * @param writer a {@link PacketByteBuf} that you can write your own data to
	 * @param player the player that the gui should be opened on
	 */
	public static void openContainer(Identifier identifier, Consumer<PacketByteBuf> writer, ServerPlayerEntity player) {
		int syncId = 120; //Write a container sync id, does this need to change?
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(identifier.toString());
		buf.writeInt(syncId);
		writer.accept(buf);
		player.networkHandler.sendPacket(new CustomPayloadClientPacket(OPEN_CONTAINER, buf));

		if (!FACTORIES.containsKey(identifier)) {
			LOGGER.error("No container factory found for %s ", identifier.toString());
			return;
		}
		player.container = FACTORIES.get(identifier).create(player, buf);
		player.container.syncId = syncId;
		player.container.addListener(player);
	}
}
