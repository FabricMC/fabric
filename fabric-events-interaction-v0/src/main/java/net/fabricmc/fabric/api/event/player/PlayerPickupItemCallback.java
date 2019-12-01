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

package net.fabricmc.fabric.api.event.player;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callback for the player picking up an item entity.
 * Is hooked in before the item is picked up.
 *
 * <p>Upon return:
 * <ul><li>SUCCESS cancels further processing and picks up the item.
 * <li>PASS falls back to further processing. If all listeners return PASS, the item is picked up.
 * <li>FAIL cancels further processing and does not pick up the item.</ul>
 */
public interface PlayerPickupItemCallback {
	Event<PlayerPickupItemCallback> EVENT = EventFactory.createArrayBacked(PlayerPickupItemCallback.class,
			(listeners) -> (player, entity) -> {
				for (PlayerPickupItemCallback event : listeners) {
					ActionResult result = event.interact(player, entity);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			}
	);

	ActionResult interact(PlayerEntity player, ItemEntity pickupEntity);
}
