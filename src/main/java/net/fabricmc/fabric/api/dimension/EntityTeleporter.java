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

package net.fabricmc.fabric.api.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

@FunctionalInterface
public interface EntityTeleporter {

	EntityTeleporter DEFAULT_TELEPORTER = (entity, previousWorld, newWorld) -> EntityTeleporter.setEntityLocation(entity, newWorld.getSpawnPos());

	/**
	 * This is used to set the entities location in a world, and spawn a portal if required. Use setEntityLocation when setting the location
	 *
	 * @param entity the entity that is traveling between 2 dims
	 * @param previousWorld the world that the entity is traveling from
	 * @param newWorld the world that the entity is traveling true
	 */
	void teleport(Entity entity, ServerWorld previousWorld, ServerWorld newWorld);

	/**
	 *
	 * This is used to set the entities location, it must be used when moving a player
	 *
	 * @param entity the entity to set the location for
	 * @param pos the pos to move the entity to
	 */
	static void setEntityLocation(Entity entity, BlockPos pos) {
		if (entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) entity).networkHandler.teleportRequest(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
			((ServerPlayerEntity) entity).networkHandler.syncWithPlayerPosition();
		} else {
			entity.setPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
		}
	}

}
