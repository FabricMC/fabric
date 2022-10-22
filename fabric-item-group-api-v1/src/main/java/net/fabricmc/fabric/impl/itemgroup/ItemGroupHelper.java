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

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupsAccessor;

@ApiStatus.Internal
public final class ItemGroupHelper {
	private ItemGroupHelper() {
	}

	public static void appendItemGroup(FabricItemGroup itemGroup) {
		for (ItemGroup existingGroup : ItemGroups.GROUPS) {
			if (existingGroup.getId().equals(itemGroup.getId())) {
				throw new IllegalStateException("Duplicate item group: " + itemGroup.getId());
			}
		}

		final int index = ItemGroups.GROUPS.length;
		final ItemGroup[] itemGroups = ArrayUtils.add(ItemGroups.GROUPS, itemGroup);

		((ItemGroupAccessor) itemGroup).setIndex(index);
		ItemGroupsAccessor.setGroups(ItemGroupsAccessor.invokeAsArray(itemGroups));
	}
}
