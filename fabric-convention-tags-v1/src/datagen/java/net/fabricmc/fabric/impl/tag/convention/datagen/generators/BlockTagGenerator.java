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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
	static List<Block> VILLAGER_JOB_SITE_BLOCKS = List.of(
			Blocks.BARREL,
			Blocks.BLAST_FURNACE,
			Blocks.BREWING_STAND,
			Blocks.CARTOGRAPHY_TABLE,
			Blocks.CAULDRON,
			Blocks.LAVA_CAULDRON,
			Blocks.WATER_CAULDRON,
			Blocks.POWDER_SNOW_CAULDRON,
			Blocks.COMPOSTER,
			Blocks.FLETCHING_TABLE,
			Blocks.GRINDSTONE,
			Blocks.LECTERN,
			Blocks.LOOM,
			Blocks.SMITHING_TABLE,
			Blocks.SMOKER,
			Blocks.STONECUTTER
	);

	public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
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
		getOrCreateTagBuilder(ConventionalBlockTags.WOODEN_BARRELS)
				.add(Blocks.BARREL);

		generateBuddingTags();

		VILLAGER_JOB_SITE_BLOCKS.forEach(getOrCreateTagBuilder(ConventionalBlockTags.VILLAGER_JOB_SITES)::add);

		generateSandstoneTags();
	}

	private void generateSandstoneTags() {
		getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_BLOCKS)
				.addOptionalTag(ConventionalBlockTags.UNCOLORED_SANDSTONE_BLOCKS)
				.addOptionalTag(ConventionalBlockTags.RED_SANDSTONE_BLOCKS);
		getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_SLABS)
				.addOptionalTag(ConventionalBlockTags.UNCOLORED_SANDSTONE_SLABS)
				.addOptionalTag(ConventionalBlockTags.RED_SANDSTONE_SLABS);
		getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_STAIRS)
				.addOptionalTag(ConventionalBlockTags.UNCOLORED_SANDSTONE_STAIRS)
				.addOptionalTag(ConventionalBlockTags.RED_SANDSTONE_STAIRS);

		getOrCreateTagBuilder(ConventionalBlockTags.RED_SANDSTONE_BLOCKS)
				.add(Blocks.RED_SANDSTONE)
				.add(Blocks.CHISELED_RED_SANDSTONE)
				.add(Blocks.CUT_RED_SANDSTONE)
				.add(Blocks.SMOOTH_RED_SANDSTONE);
		getOrCreateTagBuilder(ConventionalBlockTags.RED_SANDSTONE_SLABS)
				.add(Blocks.RED_SANDSTONE_SLAB)
				.add(Blocks.CUT_RED_SANDSTONE_SLAB)
				.add(Blocks.SMOOTH_RED_SANDSTONE_SLAB);
		getOrCreateTagBuilder(ConventionalBlockTags.RED_SANDSTONE_STAIRS)
				.add(Blocks.RED_SANDSTONE_STAIRS)
				.add(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);

		getOrCreateTagBuilder(ConventionalBlockTags.UNCOLORED_SANDSTONE_BLOCKS)
				.add(Blocks.SANDSTONE)
				.add(Blocks.CHISELED_SANDSTONE)
				.add(Blocks.CUT_SANDSTONE)
				.add(Blocks.SMOOTH_SANDSTONE);
		getOrCreateTagBuilder(ConventionalBlockTags.UNCOLORED_SANDSTONE_SLABS)
				.add(Blocks.SANDSTONE_SLAB)
				.add(Blocks.CUT_SANDSTONE_SLAB)
				.add(Blocks.SMOOTH_SANDSTONE_SLAB);
		getOrCreateTagBuilder(ConventionalBlockTags.UNCOLORED_SANDSTONE_STAIRS)
				.add(Blocks.SANDSTONE_STAIRS)
				.add(Blocks.SMOOTH_SANDSTONE_STAIRS);
	}

	private void generateBuddingTags() {
		getOrCreateTagBuilder(ConventionalBlockTags.BUDDING_BLOCKS)
				.add(Blocks.BUDDING_AMETHYST);
		getOrCreateTagBuilder(ConventionalBlockTags.BUDS)
				.add(Blocks.SMALL_AMETHYST_BUD)
				.add(Blocks.MEDIUM_AMETHYST_BUD)
				.add(Blocks.LARGE_AMETHYST_BUD);
		getOrCreateTagBuilder(ConventionalBlockTags.CLUSTERS)
				.add(Blocks.AMETHYST_CLUSTER);
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
}
