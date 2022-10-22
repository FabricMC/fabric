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

import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.itemgroup.ItemGroupEventsImpl;

public final class ItemGroupEvents {
	/**
	 * This event allows the entries of any item group to be modified.
	 * <p/>
	 * If you know beforehand which item group you'd like to modify, use {@link #modifyEntriesEvent(ItemGroup)}
	 * or {@link #modifyEntriesEvent(Identifier)} instead.
	 * <p/>
	 * This event is invoked after those two more specific events.
	 */
	public static final Event<ModifyEntriesAll> MODIFY_ENTRIES_ALL = EventFactory.createArrayBacked(ModifyEntriesAll.class, callbacks -> (group, featureSet, entries) -> {
		for (ModifyEntriesAll callback : callbacks) {
			callback.modifyEntries(group, featureSet, entries);
		}
	});

	public static Event<ModifyEntries> modifyEntriesEvent(ItemGroup itemGroup) {
		return modifyEntriesEvent(itemGroup.getId());
	}

	public static Event<ModifyEntries> modifyEntriesEvent(Identifier groupId) {
		return ItemGroupEventsImpl.getOrCreateModifyEntriesEvent(groupId);
	}

	@FunctionalInterface
	public interface ModifyEntries {
		void modifyEntries(FeatureSet featureSet, FabricItemGroupEntries entries);
	}

	@FunctionalInterface
	public interface ModifyEntriesAll {
		void modifyEntries(ItemGroup group, FeatureSet featureSet, FabricItemGroupEntries entries);
	}
}
