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

package net.fabricmc.fabric.api.object.builder.v1.entity;

import java.util.Collection;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.Spawner;

import net.fabricmc.fabric.impl.object.builder.FabricSpawnerRegistryInternals;

/**
 * Allows registering custom spawners for entities.
 *
 * <p>Entities that need custom spawning logic use {@link Spawner}s. These are run
 * every tick by the server, and are used for entities such as Wandering Traders.</p>
 *
 * <p>Note: The registry only adds spawners to one world - the recommended use is to add a callback to
 * {@link ServerWorldEvents.LOAD} and do your spawner registration in there.
 *
 * @see net.minecraft.world.gen.Spawner
 */
public final class FabricSpawnerRegistry {
	/**
	 * Adds an entity spawner to a world.
	 *
	 * @param world the world to add the spawner to
	 * @param spawner the spawner to add
	 */
	public static void register(ServerWorld world, Spawner spawner) {
		FabricSpawnerRegistryInternals.register(world, spawner);
	}

	/**
	 * Adds a collection of entity spawners to a world.
	 *
	 * @param world the world to add the spawners to
	 * @param spawners the spawners to add
	 */
	public static void register(ServerWorld world, Collection<Spawner> spawners) {
		FabricSpawnerRegistryInternals.register(world, spawners);
	}

	/**
	 * Removes an entity spawner from a world.
	 *
	 * @param world the world to remove the spawner from
	 * @param spawner the spawner to remove
	 */
	public static void deregister(ServerWorld world, Spawner spawner) {
		FabricSpawnerRegistryInternals.deregister(world, spawner);
	}

	/**
	 * Removes a collection of entity spawners from a world.
	 *
	 * @param world the world to remove the spawners from
	 * @param spawners the spawners to remove
	 */
	public static void deregister(ServerWorld world, Collection<Spawner> spawners) {
		FabricSpawnerRegistryInternals.deregister(world, spawners);
	}
}
