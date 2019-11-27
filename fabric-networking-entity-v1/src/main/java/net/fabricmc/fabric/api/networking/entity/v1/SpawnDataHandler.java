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
import net.minecraft.util.PacketByteBuf;

/**
 * The spawn data handler for entities using fabric networking to spawn.
 *
 * @param <T> the lower-bound entity type applicable of this handler
 */
public interface SpawnDataHandler<T extends Entity> {
	/**
	 * Writes spawn data of the entity into the packet buffer.
	 *
	 * <p>This method is executed on the Server Thread.
	 *
	 * @param entity the entity
	 * @param buf the packet buffer
	 */
	void write(T entity, PacketByteBuf buf);

	/**
	 * Reads spawn data from the packet buffer to the entity .
	 *
	 * <p>This method is executed on the Client Thread. You can safely invoke client-side events from
	 * this method.
	 *
	 * @param entity the entity
	 * @param buf the packet buffer
	 */
	void read(T entity, PacketByteBuf buf);
}
