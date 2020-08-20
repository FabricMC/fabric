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

package net.fabricmc.fabric.impl.object.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.Spawner;

import net.fabricmc.fabric.mixin.object.builder.ServerWorldAccessor;

public final class FabricSpawnerRegistryInternals {
	public static void register(ServerWorld world, Spawner spawner) {
		//Copy the list, because it may be immutable
		List<Spawner> spawnerList = new ArrayList<>(((ServerWorldAccessor) world).getSpawners());
		spawnerList.add(spawner);
		((ServerWorldAccessor) world).setSpawners(spawnerList);
	}

	public static void register(ServerWorld world, Collection<Spawner> spawners) {
		//Copy the list, because it may be immutable
		List<Spawner> spawnerList = new ArrayList<>(((ServerWorldAccessor) world).getSpawners());
		spawnerList.addAll(spawners);
		((ServerWorldAccessor) world).setSpawners(spawnerList);
	}

	public static void unregister(ServerWorld world, Spawner spawner) {
		//Copy the list, because it may be immutable
		List<Spawner> spawnerList = new ArrayList<>(((ServerWorldAccessor) world).getSpawners());
		spawnerList.remove(spawner);
		((ServerWorldAccessor) world).setSpawners(spawnerList);
	}

	public static void unregister(ServerWorld world, Collection<Spawner> spawners) {
		//Copy the list, because it may be immutable
		List<Spawner> spawnerList = new ArrayList<>(((ServerWorldAccessor) world).getSpawners());
		spawnerList.removeAll(spawners);
		((ServerWorldAccessor) world).setSpawners(spawnerList);
	}
}
