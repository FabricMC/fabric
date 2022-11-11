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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;

@Mixin(ItemGroups.class)
public class ItemGroupsMixin {
	@Shadow
	@Final
	private static ItemGroup BUILDING_BLOCKS;
	@Shadow
	@Final
	private static ItemGroup COLORED_BLOCKS;
	@Shadow
	@Final
	private static ItemGroup NATURAL;
	@Shadow
	@Final
	private static ItemGroup FUNCTIONAL;
	@Shadow
	@Final
	private static ItemGroup REDSTONE;
	@Shadow
	@Final
	private static ItemGroup HOTBAR;
	@Shadow
	@Final
	private static ItemGroup SEARCH;
	@Shadow
	@Final
	private static ItemGroup TOOLS;
	@Shadow
	@Final
	private static ItemGroup COMBAT;
	@Shadow
	@Final
	private static ItemGroup FOOD_AND_DRINK;
	@Shadow
	@Final
	private static ItemGroup INGREDIENTS;
	@Shadow
	@Final
	private static ItemGroup SPAWN_EGGS;
	@Shadow
	@Final
	private static ItemGroup OPERATOR;
	@Shadow
	@Final
	private static ItemGroup INVENTORY;

	@Unique
	private static final int TABS_PER_PAGE = 10;

	@Inject(method = "collect", at = @At("HEAD"), cancellable = true)
	private static void collect(ItemGroup[] groups, CallbackInfoReturnable<List<ItemGroup>> cir) {
		final List<ItemGroup> vanillaGroups = List.of(BUILDING_BLOCKS, COLORED_BLOCKS, NATURAL, FUNCTIONAL, REDSTONE, HOTBAR, SEARCH, TOOLS, COMBAT, FOOD_AND_DRINK, INGREDIENTS, SPAWN_EGGS, OPERATOR, INVENTORY);

		for (ItemGroup vanillaGroup : vanillaGroups) {
			Objects.requireNonNull(vanillaGroup);
		}

		int count = 0;

		for (ItemGroup itemGroup : groups) {
			final FabricItemGroup fabricItemGroup = (FabricItemGroup) itemGroup;

			if (vanillaGroups.contains(itemGroup)) {
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

		for (ItemGroup itemGroup : groups) {
			final FabricItemGroup fabricItemGroup = (FabricItemGroup) itemGroup;
			final String displayName = itemGroup.getDisplayName().getString();
			final var position = new ItemGroupPosition(itemGroup.getRow(), itemGroup.getColumn(), fabricItemGroup.getPage());
			final String existingName = map.put(position, displayName);

			if (existingName != null) {
				throw new IllegalArgumentException("Duplicate position: (%s) for item groups %s vs %s".formatted(position, displayName, existingName));
			}
		}

		cir.setReturnValue(List.of(groups));
	}
}
