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

package net.fabricmc.fabric.impl.networking.entity.v1;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.netty.buffer.Unpooled;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.entity.v1.EntityNetworking;
import net.fabricmc.fabric.api.networking.entity.v1.SpawnDataHandler;

public final class FabricEntityNetworking implements EntityNetworking {
	public static final FabricEntityNetworking INSTANCE = new FabricEntityNetworking();
	private final Map<EntityType<?>, SpawnDataHandler<?>> handlers = new HashMap<>();

	private FabricEntityNetworking() {
		ClientSidePacketRegistry.INSTANCE.register(FabricEntityNetworkingSettings.SPAWN_ENTITY_CHANNEL, this::receiveSpawnPacket);
	}

	@Override
	public Packet<?> createSpawnPacket(Entity entity) {
		EntityType<?> type = entity.getType();
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeString(Registry.ENTITY_TYPE.getId(type).toString());
		buf.writeVarInt(entity.getEntityId());
		buf.writeUuid(entity.getUuid());

		@SuppressWarnings("unchecked")
		SpawnDataHandler<? super Entity> handler = (SpawnDataHandler<? super Entity>) handlers.get(type);
		if (handler != null) handler.write(entity, buf);

		return ServerSidePacketRegistry.INSTANCE.toPacket(FabricEntityNetworkingSettings.SPAWN_ENTITY_CHANNEL, buf);
	}

	private void receiveSpawnPacket(PacketContext context, PacketByteBuf buf) {
		String typeId = buf.readString();
		int networkId = buf.readVarInt();
		UUID uuid = buf.readUuid();

		EntityType<?> type = Registry.ENTITY_TYPE.getOrEmpty(Identifier.tryParse(buf.readString())).orElse(null);

		if (type == null) {
			if (FabricEntityNetworkingSettings.WARN_INVALID_FABRIC_PACKET) {
				FabricEntityNetworkingSettings.LOGGER.warn("Received spawn packet for unknown entity type \"{}\"", typeId);
			}

			buf.release();
			return;
		}

		ClientWorld world = (ClientWorld) context.getPlayer().world;

		context.getTaskQueue().execute(() -> {
			Entity entity = type.create(world);

			if (entity == null) {
				if (FabricEntityNetworkingSettings.WARN_INVALID_FABRIC_PACKET) {
					FabricEntityNetworkingSettings.LOGGER.warn("Received invalid spawn packet for entity type \"{}\"", typeId);
				}

				buf.release();
				return;
			}

			entity.setEntityId(networkId);
			entity.setUuid(uuid);

			@SuppressWarnings("unchecked")
			SpawnDataHandler<? super Entity> handler = (SpawnDataHandler<? super Entity>) handlers.get(type);
			if (handler != null) handler.read(entity, buf);

			world.addEntity(networkId, entity);
			buf.release();
		});
	}

	@Override
	public <T extends Entity> void registerSpawnPacketHandler(EntityType<T> type, SpawnDataHandler<? super T> handler) {
		handlers.put(type, handler);
	}
}
