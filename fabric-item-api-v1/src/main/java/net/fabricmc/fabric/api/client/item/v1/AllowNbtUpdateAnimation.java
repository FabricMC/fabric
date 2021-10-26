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

package net.fabricmc.fabric.api.client.item.v1;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.event.AutoInvokingEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * When the NBT of an item stack changes, vanilla runs an "update animation" for the rendered main hand or off hand stack.
 * This event allows listeners to cancel this animation, which can be undesired for items that update their NBT constantly.
 *
 * <p>If the item in the main or off-hand implements this interface, there is no need for event registration. It will be called automatically.
 * The easiest way to prevent the animation entirely is to implement this interface on the {@link Item} class, and always return false.
 * Despite being only relevant client-side, this class can be safely loaded on dedicated server environments to support this.
 */
public interface AllowNbtUpdateAnimation {
	@AutoInvokingEvent
	Event<AllowNbtUpdateAnimation> EVENT = EventFactory.createArrayBacked(AllowNbtUpdateAnimation.class, listeners -> (player, hand, originalStack, updatedStack) -> {
		if (originalStack.getItem() instanceof AllowNbtUpdateAnimation listener) {
			if (!listener.allowNbtUpdateAnimation(player, hand, originalStack, updatedStack)) {
				return false;
			}
		}

		for (AllowNbtUpdateAnimation listener : listeners) {
			if (!listener.allowNbtUpdateAnimation(player, hand, originalStack, updatedStack)) {
				return false;
			}
		}

		return true;
	});

	/**
	 * Return true if this listener allows the update animation to run.
	 *
	 * @param player The current player. This may be safely cast to {@link ClientPlayerEntity} in client-only code.
	 * @param hand The hand. This event applies both to the main hand and the off hand.
	 * @param originalStack The previous stack.
	 * @param updatedStack The new stack, of the same item as {@code originalStack}.
	 * @return False to cancel the update animation, true to allow the animation (unless subsequent listeners cancel it).
	 */
	boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack originalStack, ItemStack updatedStack);
}
