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

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

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
		protected void addItems(FeatureSet featureSet, StackAdder adder) {
			adder.addAll(Registry.ITEM.stream()
					.map(ItemStack::new)
					.toList());
		}
	};

	@Override
	public void onInitialize() {
		TEST_ITEM = Registry.register(Registry.ITEM, new Identifier("fabric-item-groups-v0-testmod", "item_test_group"), new Item(new Item.Settings()));

		// Exactly two pages of item groups
		for (int i = 3; i < 10; i++) {
			Item iconItem = Registry.ITEM.get(i);
			new FabricItemGroup(new Identifier(MOD_ID, "test_group_" + i)) {
				@Override
				public ItemStack createIcon() {
					return new ItemStack(iconItem);
				}

				@Override
				protected void addItems(FeatureSet featureSet, StackAdder adder) {
					adder.add(new ItemStack(TEST_ITEM));
				}
			};
		}
	}
}
