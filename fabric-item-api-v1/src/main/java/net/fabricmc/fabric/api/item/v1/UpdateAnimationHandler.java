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

package net.fabricmc.fabric.api.item.v1;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * When the NBT of an item stack in the main hand or off hand changes, vanilla runs an "update animation".
 * If registered, an instance is called on the client side when the NBT or count of the stack has changed, but not the item,
 * and returning true allows canceling this animation.
 *
 * <p>Can be set with {@link FabricItemSettings#nbtUpdateAnimation}.
 */
@FunctionalInterface
public interface UpdateAnimationHandler {
	/**
	 * {@return true to run the vanilla NBT update animation, false to cancel it}
	 *
	 * @param player   the current player; this may be safely cast to {@link ClientPlayerEntity} in client-only code
	 * @param hand     the hand; this function applies both to the main hand and the off hand
	 * @param oldStack the previous stack, of this item
	 * @param newStack the new stack, also of this item
	 */
	boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack);
}
