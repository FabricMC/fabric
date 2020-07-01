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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Heightmap;

import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;

/**
 * Allows registering spawn restrictions for mob entities.
 */
public final class FabricSpawnRestrictionRegistry {
	/**
	 * Registers a spawn restriction entry for a type of mob entity.
	 *
	 * <p>Example:
	 * <pre>FabricSpawnRestrictionRegistry.register(
	 *  EntityType.BAT,
	 *  SpawnRestriction.Location.ON_GROUND,
	 *  Type.MOTION_BLOCKING_NO_LEAVES,
	 *  BatEntity::canSpawn);</pre>
	 * </p>
	 *
	 * @param type      the entity type
	 * @param location  the environment type where the mob can spawn
	 * @param heightmap the heightmap type
	 * @param predicate a spawn predicate
	 */
	public static <T extends MobEntity> void register(EntityType<T> type, SpawnRestriction.Location location, Heightmap.Type heightmap, SpawnRestriction.SpawnPredicate<T> predicate) {
		SpawnRestrictionAccessor.callRegister(type, location, heightmap, predicate);
	}
}
