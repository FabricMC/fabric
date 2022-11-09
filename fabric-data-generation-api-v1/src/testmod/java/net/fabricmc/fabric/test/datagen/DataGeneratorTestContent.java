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

package net.fabricmc.fabric.test.datagen;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registries;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

public class DataGeneratorTestContent implements ModInitializer {
	public static final String MOD_ID = "fabric-data-gen-api-v1-testmod";

	public static Block SIMPLE_BLOCK;
	public static Block BLOCK_WITHOUT_ITEM;
	public static Block BLOCK_WITHOUT_LOOT_TABLE;

	public static final ItemGroup SIMPLE_ITEM_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "simple"))
			// todo 22w45a
//			.entries(new ItemGroup.EntryCollector() {
//				@Override
//				public void accept(FeatureSet enabledFeatures, ItemGroup.Entries entries, boolean operatorEnabled) {
//
//					SIMPLE_BLOCK.asItem();
//				}
//			})
			.build();

	@Override
	public void onInitialize() {
		SIMPLE_BLOCK = createBlock("simple_block", true);
		BLOCK_WITHOUT_ITEM = createBlock("block_without_item", false);
		BLOCK_WITHOUT_LOOT_TABLE = createBlock("block_without_loot_table", false);
	}

	private static Block createBlock(String name, boolean hasItem) {
		Identifier identifier = new Identifier(MOD_ID, name);
		Block block = Registry.register(Registries.BLOCK, identifier, new Block(AbstractBlock.Settings.of(Material.STONE)));

		if (hasItem) {
			Item item = Registry.register(Registries.ITEM, identifier, new BlockItem(block, new Item.Settings()));
		}

		return block;
	}
}
