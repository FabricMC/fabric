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

package net.fabricmc.fabric.impl.itemgroup;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

public class ItemGroupEventsImpl {
	private static final Map<RegistryKey<ItemGroup>, Event<ItemGroupEvents.ModifyEntries>> ITEM_GROUP_EVENT_MAP = new HashMap<>();

	public static Event<ItemGroupEvents.ModifyEntries> getOrCreateModifyEntriesEvent(RegistryKey<ItemGroup> registryKey) {
		return ITEM_GROUP_EVENT_MAP.computeIfAbsent(registryKey, (g -> createModifyEvent()));
	}

	@Nullable
	public static Event<ItemGroupEvents.ModifyEntries> getModifyEntriesEvent(RegistryKey<ItemGroup> registryKey) {
		return ITEM_GROUP_EVENT_MAP.get(registryKey);
	}

	private static Event<ItemGroupEvents.ModifyEntries> createModifyEvent() {
		return EventFactory.createArrayBacked(ItemGroupEvents.ModifyEntries.class, callbacks -> (entries) -> {
			for (ItemGroupEvents.ModifyEntries callback : callbacks) {
				callback.modifyEntries(entries);
			}
		});
	}
}
