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

package net.fabricmc.fabric.api.transfer.v1.context;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * A context for interaction with item-provided apis, bound to a specific ItemKey that must match that
 * provided to {@link ItemApiLookup#get}.
 *
 * <p>In many cases such as bucket filling/emptying, it is necessary to add stacks other than the current stack.
 * For example, filling a bottle that is in a stack requires putting the water bottle in the inventory.
 */
public interface ContainerItemContext {
	/**
	 * Get the current count. If the ItemKey is not present anymore, return 0 instead.
	 */
	int getCount();

	/**
	 * Transform some of the bound items into another item key.
	 * @param count How much to transform, must be positive.
	 * @param into The target item key.
	 * @return whether the transformation was successful
	 * @throws RuntimeException If the passed count is zero or negative.
	 * @throws RuntimeException If there aren't enough items to replace, that is if {@link ContainerItemContext#getCount this.getCount()} < count.
	 */
	boolean transform(int count, ItemKey into, Transaction transaction);

	StorageFunction<ItemKey> insertionFunction();

	static ContainerItemContext ofPlayerHand(PlayerEntity player, Hand hand) {
		//return PlayerEntityContainerItemContext.ofHand(player, hand);
		throw new RuntimeException("NYI"); // TODO
	}

	static ContainerItemContext ofPlayerCursor(PlayerEntity player) {
		//return PlayerEntityContainerItemContext.ofCursor(player);
		throw new RuntimeException("NYI"); // TODO
	}

	/**
	 * A context for single stack, that will mutate it directly.
	 */
	static ContainerItemContext ofStack(ItemStack stack) {
		//return new StackContainerItemContext(stack);
		throw new RuntimeException("NYI"); // TODO
	}
}
