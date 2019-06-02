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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ItemGroupMod implements ModInitializer {
	@Override
	public void onInitialize() {
		//This creates your standard Item Group
		ItemGroup group = FabricItemGroupBuilder.build(new Identifier("fabric", "fabric_test_tab"), () -> new ItemStack(Items.IRON_CHESTPLATE));
		Item testItem = new Item(new Item.Settings().itemGroup(group));
		Registry.ITEM.add(new Identifier("fabric_test", "itemgroup"), testItem);

		//Creates a tab with all items (including ones that dont show in search such as the command block)
		FabricItemGroupBuilder.create(new Identifier("fabric", "all")).appendItems(itemStacks -> Registry.ITEM.forEach(item -> itemStacks.add(new ItemStack(item)))).build();

		//Creates a group with all modded items in
		FabricItemGroupBuilder.create(new Identifier("fabric", "modded")).appendItems(itemStacks -> Registry.ITEM.forEach(item -> {
			if (!Registry.ITEM.getId(item).getNamespace().equals("minecraft")) {
				itemStacks.add(new ItemStack(item));
			}
		})).icon(() -> new ItemStack(Blocks.TNT)).build();


		//These are just padding to ensure more than one page works
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

	}
}
