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

package net.fabricmc.fabric.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.dimension.DimensionType;


/**
 * Callback for teleporting between dimensions.
 * Called before the teleportation takes place.
 * Upon return:
 * - SUCCESS cancels further processing and teleports the player.
 * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available.
 * - FAIL cancels further processing and does not teleport the player.
 */
public interface DimensionTeleportCallback {

	Event<DimensionTeleportCallback> EVENT = EventFactory.createArrayBacked(DimensionTeleportCallback.class,
		listeners -> (entity, from, to) -> {
			for(DimensionTeleportCallback event : listeners) {
				ActionResult result = event.placeEntity(entity, from, to);
				if(result != ActionResult.PASS) {
					return result;
				}
			}

			return ActionResult.SUCCESS;
		});

	/**
	 * fired to allow mods to intercept the teleportation of an entity to another dimension
	 * this hook can take priority over default behaviour, or even cancel the teleportation
	 *
	 * @return an {@link ActionResult} to determine further processing of the teleport
	 */
	ActionResult placeEntity(Entity entity, DimensionType from, DimensionType to);
}

