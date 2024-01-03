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

package net.fabricmc.fabric.impl.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.impl.tag.convention.TagRegistration;
import net.fabricmc.fabric.mixin.item.shears.accessors.DirectRegistryEntryListAccessor;

public final class ShearsHelper implements ModInitializer {
	public static final TagKey<Item> FABRIC_SHEARS = TagRegistration.ITEM_TAG_REGISTRATION.registerFabric("shears");
	public static final List<RegistryEntryList<Item>> MATCH_TOOL_REGISTRY_ENTRIES = new ArrayList<>();

	@Override
	@SuppressWarnings("deprecation")
	public void onInitialize() {
		Set<RegistryEntry<Item>> shearsItems = new HashSet<>(); // holds all the `ShearsItem`s
		RegistryEntry<Item> shearsRegistryEntry = Items.SHEARS.getRegistryEntry();

		// this adds all `ShearsItem`s and items in #fabric:shears into all the `MatchToolLootCondition`s that contain shears
		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
			if (client) {
				return;
			}

			if (shearsItems.isEmpty()) { // fill it if it's empty
				for (Item item : Registries.ITEM) {
					if (item instanceof ShearsItem) {
						shearsItems.add(item.getRegistryEntry());
					}
				}
			}

			Set<RegistryEntry<Item>> shears = ImmutableSet.<RegistryEntry<Item>>builderWithExpectedSize(shearsItems.size())
					.addAll(Registries.ITEM.iterateEntries(FABRIC_SHEARS))
					.addAll(shearsItems)
					.build(); // use ImmutableSet for performance when using addAll on an ImmutableList builder

			for (RegistryEntryList<Item> entryList : MATCH_TOOL_REGISTRY_ENTRIES) {
				if (entryList.contains(shearsRegistryEntry)) {
					@SuppressWarnings("unchecked") // `ItemPredicate`s' RegistryEntryList should ALWAYS be Direct
					DirectRegistryEntryListAccessor<Item> accessor = (DirectRegistryEntryListAccessor<Item>) entryList;
					ImmutableList.Builder<RegistryEntry<Item>> builder = new ImmutableList.Builder<>();
					builder.addAll(accessor.getEntries());
					builder.addAll(shears);
					accessor.setEntries(builder.build());
					accessor.setEntrySet(null);
				}
			}

			MATCH_TOOL_REGISTRY_ENTRIES.clear();
		});
	}
}
