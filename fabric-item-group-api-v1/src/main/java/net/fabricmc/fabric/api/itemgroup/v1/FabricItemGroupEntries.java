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

package net.fabricmc.fabric.api.itemgroup.v1;

import java.util.function.Predicate;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * This interface is automatically implemented onto {@link ItemGroup.Entries} via Mixin and interface injection.
 */
public interface FabricItemGroupEntries {
	/**
	 * Adds the {@link ItemStack} before the first match, if no matches the {@link ItemStack} is appended to the end of the {@link ItemGroup}.
	 *
	 * @param predicate
	 * @param stack
	 * @param visibility
	 */
	default void addBefore(Predicate<ItemStack> predicate, ItemStack stack, ItemGroup.StackVisibility visibility) {
		throw new AssertionError("not implemented");
	}

	default void addBefore(ItemConvertible item, ItemStack newStack) {
		addBefore(test -> test.getItem() == item.asItem(), newStack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
	}

	default void addBefore(ItemStack stack, ItemStack newStack) {
		addBefore(test -> test.isItemEqual(stack), newStack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
	}

	default void addBefore(ItemConvertible item, ItemConvertible newItem) {
		addBefore(item, new ItemStack(newItem));
	}

	default void addBefore(ItemStack stack, ItemConvertible newItem) {
		addBefore(stack, new ItemStack(newItem));
	}

	/**
	 * Adds the {@link ItemStack} after the last mach, if no matches the {@link ItemStack} is appended to the end of the {@link ItemGroup}.
	 *
	 * @param predicate
	 * @param stack
	 * @param visibility
	 */
	default void addAfter(Predicate<ItemStack> predicate, ItemStack stack, ItemGroup.StackVisibility visibility) {
		throw new AssertionError("not implemented");
	}

	default void addAfter(ItemConvertible item, ItemStack newStack) {
		addAfter(test -> test.getItem() == item.asItem(), newStack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
	}

	default void addAfter(ItemStack stack, ItemStack newStack) {
		addAfter(test -> test.isItemEqual(stack), newStack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
	}

	default void addAfter(ItemConvertible item, ItemConvertible newItem) {
		addAfter(item, new ItemStack(newItem));
	}

	default void addAfter(ItemStack stack, ItemConvertible newItem) {
		addAfter(stack, new ItemStack(newItem));
	}
}
