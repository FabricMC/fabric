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

public final class EntityEvents {
	/**
	 * An event that is called after an entity is directly responsible for killing another entity.
	 */
	public static final Event<EntityEvents.AfterKilledOther> AFTER_KILLED_OTHER = EventFactory.createArrayBacked(AfterKilledOther.class, callbacks -> (world, entity, killed) -> {
		for (AfterKilledOther callback : callbacks) {
			callback.afterKilledOther(world, entity, killed);
		}
	});

	public interface AfterKilledOther {
		/**
		 * Called after an entity has killed another entity.
		 *
		 * @param world the world
		 * @param entity the entity
		 * @param killed the entity which was killed
		 */
		void afterKilledOther(ServerWorld world, Entity entity, LivingEntity killed);
	}

	private EntityEvents() {
	}
}
