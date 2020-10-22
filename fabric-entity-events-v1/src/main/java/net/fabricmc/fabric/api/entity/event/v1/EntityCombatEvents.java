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

package net.fabricmc.fabric.api.entity.event.v1;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events related to entities in combat.
 */
public final class EntityCombatEvents {
	/**
	 * An event that is called after an entity is directly responsible for killing another entity.
	 */
	public static final Event<AfterKilledOtherEntity> AFTER_KILLED_OTHER_ENTITY = EventFactory.createArrayBacked(AfterKilledOtherEntity.class, callbacks -> (world, entity, killed) -> {
		for (AfterKilledOtherEntity callback : callbacks) {
			callback.afterKilledOtherEntity(world, entity, killed);
		}
	});

	@FunctionalInterface
	public interface AfterKilledOtherEntity {
		/**
		 * Called after an entity has killed another entity.
		 *
		 * @param world the world
		 * @param entity the entity
		 * @param killed the entity which was killed
		 */
		void afterKilledOtherEntity(ServerWorld world, Entity entity, LivingEntity killed);
	}

	private EntityCombatEvents() {
	}
}
