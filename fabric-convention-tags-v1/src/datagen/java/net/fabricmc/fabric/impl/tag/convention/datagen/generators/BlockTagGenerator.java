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

package net.fabricmc.fabric.impl.tag.convention.datagen.generators;

import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
	public BlockTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(ConventionalBlockTags.QUARTZ_ORES)
				.add(Blocks.NETHER_QUARTZ_ORE);
		getOrCreateTagBuilder(ConventionalBlockTags.ORES)
				.addOptionalTag(BlockTags.REDSTONE_ORES)
				.addOptionalTag(BlockTags.COPPER_ORES)
				.addOptionalTag(BlockTags.GOLD_ORES)
				.addOptionalTag(BlockTags.IRON_ORES)
				.addOptionalTag(BlockTags.COAL_ORES)
				.addOptionalTag(BlockTags.EMERALD_ORES)
				.addOptionalTag(BlockTags.LAPIS_ORES)
				.addOptionalTag(BlockTags.DIAMOND_ORES)
				.addOptionalTag(ConventionalBlockTags.QUARTZ_ORES);
		getOrCreateTagBuilder(ConventionalBlockTags.CHESTS)
				.add(Blocks.CHEST)
				.add(Blocks.ENDER_CHEST)
				.add(Blocks.TRAPPED_CHEST);
		getOrCreateTagBuilder(ConventionalBlockTags.BOOKSHELVES)
				.add(Blocks.BOOKSHELF);
		generateGlassTags();
		generateShulkerTag();
		generateJobSiteBlocksTag();
		generateSandstoneBlocksTag();
	}

	private void generateShulkerTag() {
		getOrCreateTagBuilder(ConventionalBlockTags.SHULKER_BOXES)
				.add(Blocks.SHULKER_BOX)
				.add(Blocks.BLUE_SHULKER_BOX)
				.add(Blocks.BROWN_SHULKER_BOX)
				.add(Blocks.CYAN_SHULKER_BOX)
				.add(Blocks.GRAY_SHULKER_BOX)
				.add(Blocks.GREEN_SHULKER_BOX)
				.add(Blocks.LIGHT_BLUE_SHULKER_BOX)
				.add(Blocks.LIGHT_GRAY_SHULKER_BOX)
				.add(Blocks.LIME_SHULKER_BOX)
				.add(Blocks.MAGENTA_SHULKER_BOX)
				.add(Blocks.ORANGE_SHULKER_BOX)
				.add(Blocks.PINK_SHULKER_BOX)
				.add(Blocks.PURPLE_SHULKER_BOX)
				.add(Blocks.RED_SHULKER_BOX)
				.add(Blocks.WHITE_SHULKER_BOX)
				.add(Blocks.YELLOW_SHULKER_BOX)
				.add(Blocks.BLACK_SHULKER_BOX);
	}

	private void generateGlassTags() {
		getOrCreateTagBuilder(ConventionalBlockTags.GLASS_BLOCKS)
				.add(Blocks.GLASS)
				.add(Blocks.GRAY_STAINED_GLASS)
				.add(Blocks.BLACK_STAINED_GLASS)
				.add(Blocks.ORANGE_STAINED_GLASS)
				.add(Blocks.BLUE_STAINED_GLASS)
				.add(Blocks.BROWN_STAINED_GLASS)
				.add(Blocks.CYAN_STAINED_GLASS)
				.add(Blocks.GREEN_STAINED_GLASS)
				.add(Blocks.LIGHT_BLUE_STAINED_GLASS)
				.add(Blocks.LIGHT_GRAY_STAINED_GLASS)
				.add(Blocks.LIME_STAINED_GLASS)
				.add(Blocks.MAGENTA_STAINED_GLASS)
				.add(Blocks.PINK_STAINED_GLASS)
				.add(Blocks.PURPLE_STAINED_GLASS)
				.add(Blocks.RED_STAINED_GLASS)
				.add(Blocks.TINTED_GLASS)
				.add(Blocks.WHITE_STAINED_GLASS)
				.add(Blocks.YELLOW_STAINED_GLASS);
		getOrCreateTagBuilder(ConventionalBlockTags.GLASS_PANES)
				.add(Blocks.GLASS_PANE)
				.add(Blocks.GRAY_STAINED_GLASS_PANE)
				.add(Blocks.BLACK_STAINED_GLASS_PANE)
				.add(Blocks.ORANGE_STAINED_GLASS_PANE)
				.add(Blocks.BLUE_STAINED_GLASS_PANE)
				.add(Blocks.BROWN_STAINED_GLASS_PANE)
				.add(Blocks.CYAN_STAINED_GLASS_PANE)
				.add(Blocks.GREEN_STAINED_GLASS_PANE)
				.add(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE)
				.add(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE)
				.add(Blocks.LIME_STAINED_GLASS_PANE)
				.add(Blocks.MAGENTA_STAINED_GLASS_PANE)
				.add(Blocks.PINK_STAINED_GLASS_PANE)
				.add(Blocks.PURPLE_STAINED_GLASS_PANE)
				.add(Blocks.RED_STAINED_GLASS_PANE)
				.add(Blocks.WHITE_STAINED_GLASS_PANE)
				.add(Blocks.YELLOW_STAINED_GLASS_PANE);
	}

	private void generateJobSiteBlocksTag() {
		getOrCreateTagBuilder(ConventionalBlockTags.JOB_SITE_BLOCKS)
				.add(Blocks.BARREL)
				.add(Blocks.BLAST_FURNACE)
				.add(Blocks.BREWING_STAND)
				.add(Blocks.CARTOGRAPHY_TABLE)
				.add(Blocks.CAULDRON)
				.add(Blocks.COMPOSTER)
				.add(Blocks.FLETCHING_TABLE)
				.add(Blocks.GRINDSTONE)
				.add(Blocks.LECTERN)
				.add(Blocks.LOOM)
				.add(Blocks.SMITHING_TABLE)
				.add(Blocks.SMOKER)
				.add(Blocks.STONECUTTER);
	}

	private void generateSandstoneBlocksTag() {
		getOrCreateTagBuilder(ConventionalBlockTags.YELLOW_SANDSTONE_BLOCKS)
				.add(Blocks.SANDSTONE)
				.add(Blocks.CUT_SANDSTONE_SLAB)
				.add(Blocks.CHISELED_SANDSTONE)
				.add(Blocks.SMOOTH_SANDSTONE);
		getOrCreateTagBuilder(ConventionalBlockTags.RED_SANDSTONE_BLOCKS)
				.add(Blocks.RED_SANDSTONE)
				.add(Blocks.CUT_RED_SANDSTONE_SLAB)
				.add(Blocks.CHISELED_RED_SANDSTONE)
				.add(Blocks.SMOOTH_RED_SANDSTONE);
		getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_BLOCKS)
				.addOptionalTag(ConventionalBlockTags.YELLOW_SANDSTONE_BLOCKS)
				.addOptionalTag(ConventionalBlockTags.RED_SANDSTONE_BLOCKS);
	}
}
