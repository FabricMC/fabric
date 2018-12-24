/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.itemgroup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemGroupMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		new FabricItemGroupBuilder().icon(() -> new ItemStack(Items.APPLE)).create(new Identifier("fabric", "test1"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test2"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test3"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test4"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test5"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test6"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test7"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test8"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test9"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test10"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test11"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test12"));
		new FabricItemGroupBuilder().create(new Identifier("fabric", "test13"));
		ItemGroup group = new FabricItemGroupBuilder().create(new Identifier("fabric", "test14"));

		Item testItem = new Item(new Item.Settings().itemGroup(group));
		Registry.ITEM.register(new Identifier("fabric_test", "itemgroup"), testItem);
	}
}
