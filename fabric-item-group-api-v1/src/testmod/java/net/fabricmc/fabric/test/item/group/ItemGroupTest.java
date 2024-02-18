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

import com.google.common.base.Supplier;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

public class ItemGroupTest implements ModInitializer {
	private static final String MOD_ID = "fabric-item-group-api-v1-testmod";
	private static Item TEST_ITEM;

	private static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, "test_group"));

	@Override
	public void onInitialize() {
		TEST_ITEM = Registry.register(Registries.ITEM, new Identifier("fabric-item-groups-v0-testmod", "item_test_group"), new Item(new Item.Settings()));

		Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
				.displayName(Text.literal("Test Item Group"))
				.icon(() -> new ItemStack(Items.DIAMOND))
				.entries((context, entries) -> {
					entries.addAll(Registries.ITEM.stream()
							.map(ItemStack::new)
							.filter(input -> !input.isEmpty())
							.toList());
				})
				.build());

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((content) -> {
			content.add(TEST_ITEM);

			content.addBefore(Blocks.OAK_FENCE, Items.DIAMOND, Items.DIAMOND_BLOCK);
			content.addAfter(Blocks.OAK_DOOR, Items.EMERALD, Items.EMERALD_BLOCK);

			// Test adding when the existing entry does not exist.
			content.addBefore(Blocks.BEDROCK, Items.GOLD_INGOT, Items.GOLD_BLOCK);
			content.addAfter(Blocks.BEDROCK, Items.IRON_INGOT, Items.IRON_BLOCK);
		});

		// Add a differently damaged pickaxe to all groups
		ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, content) -> {
			if (group.getIcon() == ItemStack.EMPTY) { // Leave the empty groups empty
				return;
			}

			ItemStack minDmgPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
			minDmgPickaxe.setDamage(1);
			content.prepend(minDmgPickaxe);

			ItemStack maxDmgPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
			maxDmgPickaxe.setDamage(maxDmgPickaxe.getMaxDamage() - 1);
			content.add(maxDmgPickaxe);
		});

		// Regression test for #3566
		for (int j = 0; j < 20; j++) {
			Registry.register(
					Registries.ITEM_GROUP,
					new Identifier(MOD_ID, "empty_group_" + j),
					FabricItemGroup.builder()
							.displayName(Text.literal("Empty Item Group: " + j))
							.build()
			);
		}

		for (int i = 0; i < 100; i++) {
			final int index = i;

			Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "test_group_" + i), FabricItemGroup.builder()
					.displayName(Text.literal("Test Item Group: " + i))
					.icon((Supplier<ItemStack>) () -> new ItemStack(Registries.BLOCK.get(index)))
					.entries((context, entries) -> {
						var itemStack = new ItemStack(Registries.ITEM.get(index));

						if (!itemStack.isEmpty()) {
							entries.add(itemStack);
						}
					})
					.build());
		}

		try {
			// Test to make sure that item groups must have a display name.
			FabricItemGroup.builder().build();
			throw new AssertionError();
		} catch (IllegalStateException ignored) {
			// Ignored
		}
	}
}
