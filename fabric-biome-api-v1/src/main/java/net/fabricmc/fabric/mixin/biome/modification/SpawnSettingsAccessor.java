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

package net.fabricmc.fabric.mixin.biome.modification;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;

@Mixin(SpawnSettings.class)
public interface SpawnSettingsAccessor {
	@Accessor("creatureSpawnProbability")
	@Mutable
	void fabric_setCreatureSpawnProbability(float probability);

	@Accessor("spawners")
	Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> fabric_getSpawners();

	@Accessor("spawners")
	@Mutable
	void fabric_setSpawners(Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners);

	@Accessor("spawnCosts")
	Map<EntityType<?>, SpawnSettings.SpawnDensity> fabric_getSpawnCosts();

	@Accessor("spawnCosts")
	@Mutable
	void fabric_setSpawnCosts(Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts);

	@Accessor("playerSpawnFriendly")
	@Mutable
	void fabric_setPlayerSpawnFriendly(boolean playerSpawnFriendly);
}
