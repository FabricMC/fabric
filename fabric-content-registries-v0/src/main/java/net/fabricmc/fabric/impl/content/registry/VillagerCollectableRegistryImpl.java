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

package net.fabricmc.fabric.impl.content.registry;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.api.registry.VillagerCollectableRegistry;
import net.fabricmc.fabric.mixin.content.registry.VillagerEntityAccessor;

public class VillagerCollectableRegistryImpl implements VillagerCollectableRegistry {
	@Override
	public boolean contains(ItemConvertible item) {
		makeSetMutable();

		return VillagerEntityAccessor.getGatherableItems().contains(item.asItem());
	}

	@Override
	public void add(ItemConvertible item) {
		makeSetMutable();

		VillagerEntityAccessor.getGatherableItems().add(item.asItem());
	}

	@Override
	public boolean remove(ItemConvertible item) {
		makeSetMutable();

		return VillagerEntityAccessor.getGatherableItems().remove(item.asItem());
	}

	private static void makeSetMutable() {
		Set<Item> gatherableItems = VillagerEntityAccessor.getGatherableItems();

		if (!(gatherableItems instanceof HashSet)) {
			VillagerEntityAccessor.setGatherableItems(new HashSet<>(gatherableItems));
		}
	}
}
