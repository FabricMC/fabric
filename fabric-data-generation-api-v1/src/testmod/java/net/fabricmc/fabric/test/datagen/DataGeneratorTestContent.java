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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

public class DataGeneratorTestContent implements ModInitializer {
	public static final String MOD_ID = "fabric-data-gen-api-v1-testmod";

	public static Block SIMPLE_BLOCK;
	public static Block BLOCK_WITHOUT_ITEM;
	public static Block BLOCK_WITHOUT_LOOT_TABLE;

	public static final SimpleItemGroup SIMPLE_ITEM_GROUP = new SimpleItemGroup();

	@Override
	public void onInitialize() {
		SIMPLE_BLOCK = createBlock("simple_block", true);
		BLOCK_WITHOUT_ITEM = createBlock("block_without_item", false);
		BLOCK_WITHOUT_LOOT_TABLE = createBlock("block_without_loot_table", false);
	}

	private static Block createBlock(String name, boolean hasItem) {
		Identifier identifier = new Identifier(MOD_ID, name);
		Block block = Registry.register(Registry.BLOCK, identifier, new Block(AbstractBlock.Settings.of(Material.STONE)));

		if (hasItem) {
			Item item = Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings()));
			SIMPLE_ITEM_GROUP.items.add(new ItemStack(item));
		}

		return block;
	}

	private static class SimpleItemGroup extends FabricItemGroup {
		public List<ItemStack> items = new ArrayList<>();

		SimpleItemGroup() {
			super(new Identifier(MOD_ID, "simple"));
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.BONE);
		}

		@Override
		protected void addItems(FeatureSet featureSet, Entries entries) {
			entries.addAll(items);
		}
	}
}
