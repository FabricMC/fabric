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

import net.minecraft.world.gen.Spawner;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows registering custom spawners for entities.
 *
 * <p>Entities that need custom spawning logic use {@link Spawner}s. These are run
 * every tick by the server, and are used for entities such as Wandering Traders.</p>
 *
 * @see net.minecraft.world.gen.Spawner
 */
public final class FabricSpawnerRegistry {

	/**
	 * Registers an entity spawner.
	 *
	 * @param spawner the spawner to register
	 */
	public static void register(Spawner spawner) {
		SPAWNERS.add(spawner);
	}

	/**
	 * Gets all registered spawners.
	 *
	 * @return a list of all registered spawners.
	 */
	public static List<Spawner> getAll() {
		return new ArrayList<>(SPAWNERS);
	}

	/**
	 * Internal spawner list, not exposed.
	 */
	private static final List<Spawner> SPAWNERS = new ArrayList<>();
}
