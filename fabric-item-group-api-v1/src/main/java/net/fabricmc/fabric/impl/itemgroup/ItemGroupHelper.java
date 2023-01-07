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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

import net.fabricmc.fabric.mixin.itemgroup.ItemGroupsAccessor;

public final class ItemGroupHelper {
	private ItemGroupHelper() {
	}

	/**
	 * A list of item groups, but with special groups grouped at the end.
	 */
	public static List<ItemGroup> sortedGroups = ItemGroups.getGroups();

	public static void appendItemGroup(ItemGroup itemGroup) {
		for (ItemGroup existingGroup : ItemGroups.getGroups()) {
			if (existingGroup.getId().equals(itemGroup.getId())) {
				throw new IllegalStateException("Duplicate item group: " + itemGroup.getId());
			}
		}

		var itemGroups = new ArrayList<>(ItemGroups.getGroups());
		itemGroups.add(itemGroup);

		List<ItemGroup> validated = ItemGroupsAccessor.invokeCollect(itemGroups.toArray(ItemGroup[]::new));
		ItemGroupsAccessor.setGroups(validated);
		sortedGroups = validated.stream().sorted((a, b) -> {
			if (a.isSpecial() && !b.isSpecial()) return 1;
			if (!a.isSpecial() && b.isSpecial()) return -1;
			return 0;
		}).toList();
	}
}
