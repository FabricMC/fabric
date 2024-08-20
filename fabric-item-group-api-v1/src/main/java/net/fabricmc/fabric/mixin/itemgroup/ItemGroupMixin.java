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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl;
import net.fabricmc.fabric.impl.itemgroup.ItemGroupEventsImpl;

@Mixin(ItemGroup.class)
abstract class ItemGroupMixin implements FabricItemGroupImpl {
	@Shadow
	private Collection<ItemStack> displayStacks;

	@Shadow
	private Set<ItemStack> searchTabStacks;

	@Unique
	private int page = -1;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "updateEntries", at = @At("TAIL"))
	public void getStacks(ItemGroup.DisplayContext context, CallbackInfo ci) {
		final ItemGroup self = (ItemGroup) (Object) this;
		final RegistryKey<ItemGroup> registryKey = Registries.ITEM_GROUP.getKey(self).orElseThrow(() -> new IllegalStateException("Unregistered item group : " + self));

		// Do not modify special item groups (except Operator Blocks) at all.
		// Special item groups include Saved Hotbars, Search, and Survival Inventory.
		// Note, search gets modified as part of the parent item group.
		if (self.isSpecial() && registryKey != ItemGroups.OPERATOR) return;

		// Sanity check for the injection point. It should be after these fields are set.
		Objects.requireNonNull(displayStacks, "displayStacks");
		Objects.requireNonNull(searchTabStacks, "searchTabStacks");

		// Convert the entries to lists
		var mutableDisplayStacks = new LinkedList<>(displayStacks);
		var mutableSearchTabStacks = new LinkedList<>(searchTabStacks);
		var entries = new FabricItemGroupEntries(context, mutableDisplayStacks, mutableSearchTabStacks);

		// Now trigger the events
		if (registryKey != ItemGroups.OPERATOR || context.hasPermissions()) {
			final Event<ItemGroupEvents.ModifyEntries> modifyEntriesEvent = ItemGroupEventsImpl.getModifyEntriesEvent(registryKey);

			if (modifyEntriesEvent != null) {
				modifyEntriesEvent.invoker().modifyEntries(entries);
			}

			ItemGroupEvents.MODIFY_ENTRIES_ALL.invoker().modifyEntries(self, entries);
		}

		// Convert the stacks back to sets after the events had a chance to modify them
		displayStacks.clear();
		displayStacks.addAll(mutableDisplayStacks);

		searchTabStacks.clear();
		searchTabStacks.addAll(mutableSearchTabStacks);
	}

	@Override
	public int fabric_getPage() {
		if (page < 0) {
			throw new IllegalStateException("Item group has no page");
		}

		return page;
	}

	@Override
	public void fabric_setPage(int page) {
		this.page = page;
	}
}
