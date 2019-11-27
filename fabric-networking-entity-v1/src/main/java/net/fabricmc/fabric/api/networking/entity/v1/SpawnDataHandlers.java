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

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.impl.networking.entity.v1.BuiltinSpawnDataHandlers;
import net.fabricmc.fabric.impl.networking.entity.v1.ComposedSpawnDataHandler;

/**
 * A few spawn data handlers the implementation provides and utility methods to handle them.
 */
public final class SpawnDataHandlers {
	/**
	 * Sends the position and rotation of the entity to the client.
	 */
	public static final SpawnDataHandler<Entity> POSITION_AND_ROTATION = BuiltinSpawnDataHandlers.POSITION_AND_ROTATION;
	/**
	 * Sends the velocity of the entity to the client.
	 */
	public static final SpawnDataHandler<Entity> VELOCITY = BuiltinSpawnDataHandlers.VELOCITY;
	/**
	 * Sends the head yaw of the entity to the client.
	 */
	public static final SpawnDataHandler<Entity> HEAD_YAW = BuiltinSpawnDataHandlers.HEAD_YAW;

	/**
	 * Combine a few spawn data handlers.
	 *
	 * @param entries the handlers
	 * @param <U>     the target entity type
	 * @return the combined handler
	 */
	@SafeVarargs
	public static <U extends Entity> SpawnDataHandler<U> compose(SpawnDataHandler<? super U>... entries) {
		return ComposedSpawnDataHandler.of(entries);
	}

	/**
	 * Combine a few spawn data handlers.
	 *
	 * @param entries the handlers
	 * @param <U>     the target entity type
	 * @return the combined handler
	 */
	public static <U extends Entity> SpawnDataHandler<U> compose(Iterable<SpawnDataHandler<? super U>> entries) {
		return ComposedSpawnDataHandler.of(entries);
	}

	/**
	 * Creates a client-side data handler that is effectively a callback.
	 *
	 * <p>May be useful if some action is to be taken on the clientside addition of an entity, e.g. a sound to be played
	 * for minecarts, or used for firing events.
	 *
	 * @param callback the callback for the clientside entity addition
	 * @param <U>      the entity type
	 * @return the callback wrapped in a spawn data handler
	 */
	public static <U extends Entity> SpawnDataHandler<U> spawnCallback(Consumer<U> callback) {
		return new SpawnDataHandler<U>() {
			@Override
			public void write(U entity, PacketByteBuf buf) {
			}

			@Override
			public void read(U entity, PacketByteBuf buf) {
				callback.accept(entity);
			}
		};
	}

	private SpawnDataHandlers() {
	}
}
