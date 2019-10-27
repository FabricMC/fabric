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

package net.fabricmc.fabric.api.event.client.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event handler has been deprecated due to not hooking nicely
 * into the game. Please use the alternatives.
 *
 * @deprecated 0.3.0
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public interface ClientPickBlockCallback {
	@Deprecated
	final class Container {
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

	@Deprecated Event<ClientPickBlockCallback> EVENT = EventFactory.createArrayBacked(ClientPickBlockCallback.class,
		(listeners) -> (player, result, container) -> {
			for (ClientPickBlockCallback event : listeners) {
				if (!event.pick(player, result, container)) {
					return false;
				}
			}

			return true;
		}
	);

	boolean pick(PlayerEntity player, HitResult result, Container container);
}
