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

import java.util.Objects;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

public class DataGeneratorTestContent implements ModInitializer {
	public static final String MOD_ID = "fabric-data-gen-api-v1-testmod";

	public static Block SIMPLE_BLOCK;
	public static Block BLOCK_WITHOUT_ITEM;
	public static Block BLOCK_WITHOUT_LOOT_TABLE;
	public static Block BLOCK_WITH_VANILLA_LOOT_TABLE;
	public static Block BLOCK_THAT_DROPS_NOTHING;
	public static ItemGroup SIMPLE_ITEM_GROUP;

	@Override
	public void onInitialize() {
		SIMPLE_ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "default"), () -> new ItemStack(Items.BONE));
		SIMPLE_BLOCK = createBlock("simple_block", true, AbstractBlock.Settings.of(Material.STONE));
		BLOCK_WITHOUT_ITEM = createBlock("block_without_item", false, AbstractBlock.Settings.of(Material.STONE));
		BLOCK_WITHOUT_LOOT_TABLE = createBlock("block_without_loot_table", false, AbstractBlock.Settings.of(Material.STONE));
		BLOCK_WITH_VANILLA_LOOT_TABLE = createBlock("block_with_vanilla_loot_table", false, AbstractBlock.Settings.of(Material.STONE).dropsLike(Blocks.STONE));
		BLOCK_THAT_DROPS_NOTHING = createBlock("block_that_drops_nothing", false, AbstractBlock.Settings.of(Material.STONE).dropsNothing());
	}

	private static Block createBlock(String name, boolean hasItem, AbstractBlock.Settings settings) {
		Identifier identifier = new Identifier(MOD_ID, name);
		Block block = Registry.register(Registry.BLOCK, identifier, new Block(settings));

		if (hasItem) {
			Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));

			Objects.requireNonNull(block.asItem().getGroup());
		}

		return block;
	}
}
