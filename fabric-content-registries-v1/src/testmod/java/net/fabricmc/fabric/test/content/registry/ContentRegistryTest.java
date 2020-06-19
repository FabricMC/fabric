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

package net.fabricmc.fabric.test.content.registry;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.content.registry.v1.ContentRegistries;

public class ContentRegistryTest implements ModInitializer {
	@Override
	public void onInitialize() {
		ContentRegistries.COMPOSTABLE_ITEM.add(Items.BLAZE_POWDER, 0.5f);
		ContentRegistries.COMPOSTABLE_ITEM.add(ItemTags.MUSIC_DISCS, 0.2f);
		ContentRegistries.COMPOSTABLE_ITEM.remove(Items.SUGAR_CANE);

		ContentRegistries.FLAMMABLE_FROM_FIRE.add(Blocks.EMERALD_BLOCK, 100, 50);
		ContentRegistries.FLAMMABLE_FROM_FIRE.remove(Blocks.OAK_PLANKS);
		ContentRegistries.FLAMMABLE_FROM_FIRE.remove(BlockTags.LEAVES);

		ContentRegistries.FLATTENABLE_BLOCK.add(Blocks.STONE_BRICKS, Blocks.CAKE.getDefaultState());
		ContentRegistries.FLATTENABLE_BLOCK.remove(Blocks.GRASS_BLOCK);

		ContentRegistries.FUEL_ITEM.add(Blocks.ANVIL, 20);
		ContentRegistries.FUEL_ITEM.remove(ItemTags.PLANKS);

		ContentRegistries.STRIPPABLE_BLOCK.add(Blocks.QUARTZ_PILLAR, Blocks.BONE_BLOCK);
		ContentRegistries.STRIPPABLE_BLOCK.add(Blocks.BONE_BLOCK, Blocks.STRIPPED_BIRCH_LOG);
		ContentRegistries.STRIPPABLE_BLOCK.remove(Blocks.BONE_BLOCK);
		ContentRegistries.STRIPPABLE_BLOCK.add(Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
		ContentRegistries.STRIPPABLE_BLOCK.remove(BlockTags.ACACIA_LOGS);

		ContentRegistries.TILLABLE_BLOCK.add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.ACACIA_FENCE_GATE.getDefaultState());
		ContentRegistries.TILLABLE_BLOCK.remove(Blocks.COARSE_DIRT);
	}
}
