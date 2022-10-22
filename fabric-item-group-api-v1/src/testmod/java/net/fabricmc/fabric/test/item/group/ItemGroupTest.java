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

package net.fabricmc.fabric.test.item.group;

import com.google.common.base.Preconditions;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.impl.itemgroup.MinecraftItemGroups;

public class ItemGroupTest implements ModInitializer {
	private static final String MOD_ID = "fabric-item-group-api-v1-testmod";
	private static Item TEST_ITEM;

	//Adds an item group with all items in it
	private static final ItemGroup ITEM_GROUP = new FabricItemGroup(new Identifier(MOD_ID, "test_group")) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.DIAMOND);
		}

		@Override
		protected void addItems(FeatureSet featureSet, Entries entries) {
			entries.addAll(Registry.ITEM.stream()
					.map(ItemStack::new)
					.toList());
		}
	};

	@Override
	public void onInitialize() {
		TEST_ITEM = Registry.register(Registry.ITEM, new Identifier("fabric-item-groups-v0-testmod", "item_test_group"), new Item(new Item.Settings()));

		checkAllVanillaGroupsHaveAssignedIds();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((featureSet, content) -> {
			content.add(TEST_ITEM);

			content.addBefore(Blocks.OAK_FENCE, Items.DIAMOND, Items.DIAMOND_BLOCK);
			content.addAfter(Blocks.OAK_DOOR, Items.EMERALD, Items.EMERALD_BLOCK);

			// Test adding when the existing entry does not exist.
			content.addBefore(Blocks.BEDROCK, Items.GOLD_INGOT, Items.GOLD_BLOCK);
			content.addAfter(Blocks.BEDROCK, Items.IRON_INGOT, Items.IRON_BLOCK);
		});

		// Add a differently damaged pickaxe to all groups
		ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, featureSet, content) -> {
			ItemStack minDmgPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
			minDmgPickaxe.setDamage(1);
			content.prepend(minDmgPickaxe);

			ItemStack maxDmgPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
			maxDmgPickaxe.setDamage(maxDmgPickaxe.getMaxDamage() - 1);
			content.add(maxDmgPickaxe);
		});
	}

	private static void checkAllVanillaGroupsHaveAssignedIds() {
		for (ItemGroup group : ItemGroups.GROUPS) {
			if (group instanceof FabricItemGroup) {
				continue; // Skip groups added by test mods
			}

			Preconditions.checkArgument(MinecraftItemGroups.GROUP_ID_MAP.containsKey(group),
					"Missing ID for Vanilla ItemGroup %s. Assign one in MinecraftItemGroups.", group);
		}
	}
}
