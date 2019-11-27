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

package net.fabricmc.fabric.api.networking.entity.v1;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;

import net.fabricmc.fabric.impl.networking.entity.v1.FabricEntityNetworking;

/**
 * Fabric entity networking hooks.
 */
public interface EntityNetworking {
	/**
	 * The instance offered by the implementation.
	 */
	EntityNetworking INSTANCE = FabricEntityNetworking.INSTANCE;

	/**
	 * Creates a packet to spawn a presumably modded entity.
	 *
	 * <p>This method can be used as the implementation for {@code Entity.createSpawnPacket} for
	 * modded entities.
	 *
	 * <p>To use this method, the entity's type must be
	 * {@link #registerSpawnPacketHandler(EntityType, SpawnDataHandler) registered}.
	 *
	 * @param entity the entity
	 * @return the created spawn packet
	 */
	Packet<?> createSpawnPacket(Entity entity);

	/**
	 * Registers a spawn data handler to an entity type.
	 *
	 * @param type the entity type
	 * @param handler the handler
	 * @param <T> the entity type's corresponding entity class
	 */
	<T extends Entity> void registerSpawnPacketHandler(EntityType<T> type, SpawnDataHandler<? super T> handler);
}
