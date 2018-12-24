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
		FabricItemGroupBuilder.create(new Identifier("fabric", "test1")).icon(() -> new ItemStack(Items.APPLE)).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test2")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test3")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test4")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test5")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test6")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test7")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test8")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test9")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test10")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test11")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test12")).build();
		FabricItemGroupBuilder.create(new Identifier("fabric", "test13")).build();
		ItemGroup group = FabricItemGroupBuilder.create(new Identifier("fabric", "test14")).build();

		Item testItem = new Item(new Item.Settings().itemGroup(group));
		Registry.ITEM.register(new Identifier("fabric_test", "itemgroup"), testItem);
	}
}
