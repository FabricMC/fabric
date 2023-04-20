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

import static net.minecraft.item.ItemGroups.BUILDING_BLOCKS;
import static net.minecraft.item.ItemGroups.COLORED_BLOCKS;
import static net.minecraft.item.ItemGroups.COMBAT;
import static net.minecraft.item.ItemGroups.FOOD_AND_DRINK;
import static net.minecraft.item.ItemGroups.FUNCTIONAL;
import static net.minecraft.item.ItemGroups.HOTBAR;
import static net.minecraft.item.ItemGroups.INGREDIENTS;
import static net.minecraft.item.ItemGroups.INVENTORY;
import static net.minecraft.item.ItemGroups.NATURAL;
import static net.minecraft.item.ItemGroups.OPERATOR;
import static net.minecraft.item.ItemGroups.REDSTONE;
import static net.minecraft.item.ItemGroups.SEARCH;
import static net.minecraft.item.ItemGroups.SPAWN_EGGS;
import static net.minecraft.item.ItemGroups.TOOLS;

import java.util.HashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;

@Mixin(ItemGroups.class)
public class ItemGroupsMixin {
	@Unique
	private static final int TABS_PER_PAGE = 10;

	@Inject(method = "collect", at = @At("HEAD"), cancellable = true)
	private static void collect(CallbackInfo ci) {
		final List<RegistryKey<ItemGroup>> vanillaGroups = List.of(BUILDING_BLOCKS, COLORED_BLOCKS, NATURAL, FUNCTIONAL, REDSTONE, HOTBAR, SEARCH, TOOLS, COMBAT, FOOD_AND_DRINK, INGREDIENTS, SPAWN_EGGS, OPERATOR, INVENTORY);

		int count = 0;

		for (RegistryKey<ItemGroup> registryKey : Registries.ITEM_GROUP.getKeys()) {
			final ItemGroup itemGroup = Registries.ITEM_GROUP.getOrThrow(registryKey);
			final FabricItemGroup fabricItemGroup = (FabricItemGroup) itemGroup;

			if (vanillaGroups.contains(registryKey)) {
				// Vanilla group goes on the first page.
				fabricItemGroup.setPage(0);
				continue;
			}

			final ItemGroupAccessor itemGroupAccessor = (ItemGroupAccessor) itemGroup;
			fabricItemGroup.setPage((count / TABS_PER_PAGE) + 1);
			int pageIndex = count % TABS_PER_PAGE;
			ItemGroup.Row row = pageIndex < (TABS_PER_PAGE / 2) ? ItemGroup.Row.TOP : ItemGroup.Row.BOTTOM;
			itemGroupAccessor.setRow(row);
			itemGroupAccessor.setColumn(row == ItemGroup.Row.TOP ? pageIndex % TABS_PER_PAGE : (pageIndex - TABS_PER_PAGE / 2) % (TABS_PER_PAGE));

			count++;
		}

		// Overlapping group detection logic, with support for pages.
		record ItemGroupPosition(ItemGroup.Row row, int column, int page) { }
		var map = new HashMap<ItemGroupPosition, String>();

		for (RegistryKey<ItemGroup> registryKey : Registries.ITEM_GROUP.getKeys()) {
			final ItemGroup itemGroup = Registries.ITEM_GROUP.getOrThrow(registryKey);
			final FabricItemGroup fabricItemGroup = (FabricItemGroup) itemGroup;
			final String displayName = itemGroup.getDisplayName().getString();
			final var position = new ItemGroupPosition(itemGroup.getRow(), itemGroup.getColumn(), fabricItemGroup.getPage());
			final String existingName = map.put(position, displayName);

			if (existingName != null) {
				throw new IllegalArgumentException("Duplicate position: (%s) for item groups %s vs %s".formatted(position, displayName, existingName));
			}
		}

		ci.cancel();
	}
}
