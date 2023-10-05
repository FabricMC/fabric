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
 * This event is emitted during the block-picking process. It can be used to
 * modify the returned ItemStack, as well as nullify it - returning an empty
 * ItemStack will cause the event to leave, and no block to be picked.
 */
public interface ClientPickBlockApplyCallback {
	Event<ClientPickBlockApplyCallback> EVENT = EventFactory.createArrayBacked(ClientPickBlockApplyCallback.class,
			(listeners) -> (player, result, _stack) -> {
				ItemStack stack = _stack;

				for (ClientPickBlockApplyCallback event : listeners) {
					stack = event.pick(player, result, stack);

					if (stack.isEmpty()) {
						return ItemStack.EMPTY;
					}
				}

				return stack;
			}
	);

	ItemStack pick(PlayerEntity player, HitResult result, ItemStack stack);
}
