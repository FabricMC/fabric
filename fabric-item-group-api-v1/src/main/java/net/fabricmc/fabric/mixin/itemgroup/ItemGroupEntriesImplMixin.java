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

package net.fabricmc.fabric.mixin.itemgroup;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.resource.featuretoggle.FeatureSet;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;

@Mixin(ItemGroup.EntriesImpl.class)
public abstract class ItemGroupEntriesImplMixin implements FabricItemGroupEntries {
	@Shadow
	@Final
	private ItemStackSet parentTabStacks;

	@Shadow
	@Final
	private ItemStackSet searchTabStacks;

	@Shadow
	@Final
	private ItemGroup group;

	@Shadow
	@Final
	private FeatureSet field_40190;

	@Shadow
	public abstract void add(ItemStack stack, ItemGroup.StackVisibility visibiity);

	@Override
	public void addBefore(Predicate<ItemStack> predicate, ItemStack stack, ItemGroup.StackVisibility visibility) {
		if (!isEnabled(stack, visibility)) {
			return;
		}

		switch (visibility) {
		case PARENT_AND_SEARCH_TABS -> {
			addBefore(predicate, stack, parentTabStacks);
			addBefore(predicate, stack, searchTabStacks);
		}
		case PARENT_TAB_ONLY -> addBefore(predicate, stack, parentTabStacks);
		case SEARCH_TAB_ONLY -> addBefore(predicate, stack, searchTabStacks);
		}
	}

	@Override
	public void addAfter(Predicate<ItemStack> predicate, ItemStack stack, ItemGroup.StackVisibility visibility) {
		if (!isEnabled(stack, visibility)) {
			return;
		}

		switch (visibility) {
		case PARENT_AND_SEARCH_TABS -> {
			addAfter(predicate, stack, parentTabStacks);
			addAfter(predicate, stack, searchTabStacks);
		}
		case PARENT_TAB_ONLY -> addAfter(predicate, stack, parentTabStacks);
		case SEARCH_TAB_ONLY -> addAfter(predicate, stack, searchTabStacks);
		}
	}

	@Unique
	private void addBefore(Predicate<ItemStack> predicate, ItemStack stack, ItemStackSet set) {
		ItemStack matchedStack = null;

		for (ItemStack itemStack : set) {
			if (predicate.test(itemStack)) {
				matchedStack = itemStack;
				break;
			}
		}

		if (matchedStack == null) {
			set.add(stack);
			return;
		}

		addAt(matchedStack, stack, set);
	}

	@Unique
	private void addAfter(Predicate<ItemStack> predicate, ItemStack stack, ItemStackSet set) {
		ItemStack matchedStack = null;

		for (ItemStack itemStack : set) {
			if (predicate.test(itemStack)) {
				matchedStack = itemStack;
			}
		}

		if (matchedStack == null) {
			set.add(stack);
			return;
		}

		addAt(matchedStack, stack, set);
	}

	@Unique
	private void addAt(ItemStack at, ItemStack stack, ItemStackSet set) {
		final ItemStackSet tempSet = new ItemStackSet();

		tempSet.addAll(set.headSet(at));
		tempSet.add(stack);
		tempSet.addAll(set.tailSet(at));

		set.clear();
		set.addAll(tempSet);
	}

	@Unique
	private boolean isEnabled(ItemStack stack, ItemGroup.StackVisibility visibility) {
		boolean exists = this.parentTabStacks.contains(stack) && visibility != ItemGroup.StackVisibility.SEARCH_TAB_ONLY;

		if (exists) {
			throw new IllegalStateException("%s already exists in item group: %s".formatted(stack.toHoverableText().getString(), group.getDisplayName().getString()));
		}

		return stack.getItem().method_45382(this.field_40190);
	}
}
