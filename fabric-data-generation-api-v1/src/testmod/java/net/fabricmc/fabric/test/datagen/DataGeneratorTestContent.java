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
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

public class DataGeneratorTestContent implements ModInitializer {
	public static final String MOD_ID = "fabric-data-gen-api-v1-testmod";

	public static Block SIMPLE_BLOCK;
	public static Block BLOCK_WITHOUT_ITEM;
	public static Block BLOCK_WITHOUT_LOOT_TABLE;
	public static Block BLOCK_WITH_VANILLA_LOOT_TABLE;
	public static Block BLOCK_THAT_DROPS_NOTHING;
	public static Block BLOCK_WITH_CUSTOM_MODEL_1;
	public static Block BLOCK_WITH_CUSTOM_MODEL_2;
	public static Block BLOCK_WITH_EMPTY_MODEL;

	public static Item ITEM_WITH_CUSTOM_MODEL_1;
	public static Item ITEM_WITH_CUSTOM_MODEL_2;
	public static Item ITEM_WITH_NORMAL_MODEL;

	public static final ItemGroup SIMPLE_ITEM_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "simple"))
			.icon(() -> new ItemStack(Items.DIAMOND_PICKAXE))
			.displayName(Text.translatable("fabric-data-gen-api-v1-testmod.simple_item_group"))
			.build();

	@Override
	public void onInitialize() {
		SIMPLE_BLOCK = createBlock("simple_block", true, AbstractBlock.Settings.of(Material.STONE));
		BLOCK_WITHOUT_ITEM = createBlock("block_without_item", false, AbstractBlock.Settings.of(Material.STONE));
		BLOCK_WITHOUT_LOOT_TABLE = createBlock("block_without_loot_table", false, AbstractBlock.Settings.of(Material.STONE));
		BLOCK_WITH_VANILLA_LOOT_TABLE = createBlock("block_with_vanilla_loot_table", false, AbstractBlock.Settings.of(Material.STONE).dropsLike(Blocks.STONE));
		BLOCK_THAT_DROPS_NOTHING = createBlock("block_that_drops_nothing", false, AbstractBlock.Settings.of(Material.STONE).dropsNothing());
		BLOCK_WITH_CUSTOM_MODEL_1 = createBlock("block_with_custom_model_1", false, AbstractBlock.Settings.of(Material.STONE));
		BLOCK_WITH_CUSTOM_MODEL_2 = createBlock("block_with_custom_model_2", false, AbstractBlock.Settings.of(Material.STONE));
		BLOCK_WITH_EMPTY_MODEL = createBlock("block_with_empty_model", false, AbstractBlock.Settings.of(Material.STONE));

		ITEM_WITH_CUSTOM_MODEL_1 = createItem("item_with_custom_model_1");
		ITEM_WITH_CUSTOM_MODEL_2 = createItem("item_with_custom_model_2");
		ITEM_WITH_NORMAL_MODEL = createItem("item_with_normal_model");

		ItemGroupEvents.modifyEntriesEvent(SIMPLE_ITEM_GROUP).register(entries -> entries.add(SIMPLE_BLOCK));
	}

	private static Block createBlock(String name, boolean hasItem, AbstractBlock.Settings settings) {
		Identifier identifier = new Identifier(MOD_ID, name);
		Block block = Registry.register(Registries.BLOCK, identifier, new Block(settings));

		if (hasItem) {
			Registry.register(Registries.ITEM, identifier, new BlockItem(block, new Item.Settings()));
		}

		return block;
	}

	private static Item createItem(String name) {
		Identifier identifier = new Identifier(MOD_ID, name);
		return Registry.register(Registries.ITEM, identifier, new Item(new Item.Settings()));
	}
}
