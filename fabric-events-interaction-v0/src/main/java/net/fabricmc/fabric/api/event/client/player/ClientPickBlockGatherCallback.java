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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is emitted at the beginning of the block picking process in
 * order to find any applicable ItemStack. The first non-empty ItemStack
 * will be returned, overriding vanilla behavior.
 *
 * <p>Note that this is called any time the pick key is pressed, even if there is no target block.
 * The {@link HitResult} could be a {@link BlockHitResult} or an {@link EntityHitResult}.
 * If the hit missed, it will be a {@link BlockHitResult} with {@linkplain BlockHitResult#getType() type}
 * {@link BlockHitResult.Type#MISS}, so make sure to check for that.
 */
public interface ClientPickBlockGatherCallback {
	Event<ClientPickBlockGatherCallback> EVENT = EventFactory.createArrayBacked(ClientPickBlockGatherCallback.class,
			(listeners) -> (player, result) -> {
				for (ClientPickBlockGatherCallback event : listeners) {
					ItemStack stack = event.pick(player, result);

					if (stack != ItemStack.EMPTY && !stack.isEmpty()) {
						return stack;
					}
				}

				return ItemStack.EMPTY;
			}
	);

	ItemStack pick(PlayerEntity player, HitResult result);
}
