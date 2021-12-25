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
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class DataGeneratorTestContent implements ModInitializer {
	public static final String MOD_ID = "fabric-data-gen-api-v1-testmod";

	public static Block SIMPLE_BLOCK;
	public static Block BLOCK_WITHOUT_ITEM;

	@Override
	public void onInitialize() {
		SIMPLE_BLOCK = createBlock("simple_block", true);
		BLOCK_WITHOUT_ITEM = createBlock("block_without_item", false);
	}

	private static Block createBlock(String name, boolean hasItem) {
		Identifier identifier = new Identifier(MOD_ID, name);
		Block block = Registry.register(Registry.BLOCK, identifier, new Block(AbstractBlock.Settings.of(Material.STONE)));

		if (hasItem) {
			Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));

			Objects.requireNonNull(block.asItem().getGroup());
		}

		return block;
	}
}
