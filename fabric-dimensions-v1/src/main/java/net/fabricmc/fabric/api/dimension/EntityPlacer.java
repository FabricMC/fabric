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
import net.minecraft.world.dimension.DimensionType;

import java.util.HashSet;

/**
 * Responsible for placing an Entity once they have entered a dimension.
 * Stored by a FabricDimensionType, and used in Entity::changeDimension.
 */
public abstract class EntityPlacer {

	public static final EntityPlacer DEFAULT_INSTANCE = new EntityPlacer() {
		@Override
		public void placeEntity(Entity entity, DimensionType dimensionType, ServerWorld targetWorld) {
			//NO-OP
		}
	};

	public abstract void placeEntity(Entity entity, DimensionType dimensionType, ServerWorld targetWorld);

	/**
	 * Used to set an entity's position.
	 * Must be used when repositioning.
	 * Yaw and pitch are kept from the entity's original positioning.
	 * @param entity the entity to reposition
	 * @param x new x position of the entity
	 * @param y new y position of the entity
	 * @param z new z position of the entity
	 */
	protected void setEntityPosition(Entity entity, double x, double y, double z) {
		this.setEntityPosition(entity, x, y, z, entity.yaw, entity.pitch);
	}

	/**
	 * Used to set an entity's position.
	 * Must be used when repositioning.
	 * @param entity the entity to reposition
	 * @param x new x position of the entity
	 * @param y new y position of the entity
	 * @param z new z position of the entity
	 * @param yaw new yaw of the entity
	 * @param pitch new pitch of the entity
	 */
	protected void setEntityPosition(Entity entity, double x, double y, double z, float yaw, float pitch) {
		if(entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) entity).networkHandler.teleportRequest(x, y, z, yaw, pitch, new HashSet<>());
			((ServerPlayerEntity) entity).networkHandler.syncWithPlayerPosition();
		}
		else {
			entity.setPositionAndAngles(x, y, z, yaw, pitch);
		}
	}
}
