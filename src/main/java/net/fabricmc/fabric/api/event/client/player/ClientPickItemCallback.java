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

package net.fabricmc.fabric.api.event.client.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;

/**
 * This interaction event is called on the CLIENT SIDE ONLY when the player
 * attempts to pick up an item.
 *
 * Use {@link Container} to change the picked stack. Return true if you
 * wish for execution to continue, return false to cancel the item picking
 * operation (for example, if you want to route to the server side, etc.)
 */
public interface ClientPickItemCallback {
	public static final class Container {
		private ItemStack stack;

		public Container(ItemStack stack) {
			this.stack = stack;
		}

		public ItemStack getStack() {
			return stack;
		}

		public void setStack(ItemStack stack) {
			this.stack = stack;
		}
	}

	public static final Event<ClientPickItemCallback> EVENT = EventFactory.arrayBacked(ClientPickItemCallback.class,
		(listeners) -> (player, result, container) -> {
			for (ClientPickItemCallback event : listeners) {
				if (!event.pick(player, result, container)) {
					return false;
				}
			}

			return true;
		}
	);

	boolean pick(PlayerEntity player, HitResult result, Container container);
}
