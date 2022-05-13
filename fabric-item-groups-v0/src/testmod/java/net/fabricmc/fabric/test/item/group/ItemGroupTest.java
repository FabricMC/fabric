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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

public class ItemGroupTest implements ModInitializer {
	private static Item TEST_ITEM;

	//Adds an item group with all items in it
	private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier("fabric-item-groups-v0-testmod", "test_group"))
				.icon(() -> new ItemStack(Items.DIAMOND))
				.appendItems(stacks ->
						Registry.ITEM.stream()
						.map(ItemStack::new)
						.forEach(stacks::add)
				).build();

	private static final ItemGroup ITEM_GROUP_2 = FabricItemGroupBuilder.create(new Identifier("fabric-item-groups-v0-testmod", "test_group_two"))
				.icon(() -> new ItemStack(Items.REDSTONE))
				.appendItems((stacks, itemGroup) -> {
					for (Item item : Registry.ITEM) {
						if (item.getGroup() == ItemGroup.FOOD || item.getGroup() == itemGroup) {
							stacks.add(new ItemStack(item));
						}
					}
				}).build();

	@Override
	public void onInitialize() {
		TEST_ITEM = Registry.register(Registry.ITEM, new Identifier("fabric-item-groups-v0-testmod", "item_test_group"), new Item(new Item.Settings().group(ITEM_GROUP_2)));

		// Exactly two pages of item groups
		for (int i = 3; i < 10; i++) {
			Item iconItem = Registry.ITEM.get(i);
			FabricItemGroupBuilder.build(new Identifier("fabric-item-groups-v0-testmod", "test_group_" + i), () -> new ItemStack(iconItem));
		}
	}
}
