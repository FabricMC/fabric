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

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
		getOrCreateTagBuilder(ConventionalBlockTags.ORES_QUARTZ)
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
				.addOptionalTag(ConventionalBlockTags.ORES_QUARTZ);

		getOrCreateTagBuilder(ConventionalBlockTags.CHESTS)
				.add(Blocks.CHEST)
				.add(Blocks.ENDER_CHEST)
				.add(Blocks.TRAPPED_CHEST);
		getOrCreateTagBuilder(ConventionalBlockTags.BOOKSHELVES)
				.add(Blocks.BOOKSHELF);
		generateGlassTags();
		generateShulkerTag();
		getOrCreateTagBuilder(ConventionalBlockTags.BARRELS_WOODEN)
				.add(Blocks.BARREL);
		getOrCreateTagBuilder(ConventionalBlockTags.BARRELS)
				.addTag(ConventionalBlockTags.BARRELS_WOODEN);

		generateBuddingTags();

		VILLAGER_JOB_SITE_BLOCKS.forEach(getOrCreateTagBuilder(ConventionalBlockTags.VILLAGER_JOB_SITES)::add);

		generateSandstoneTags();

		getOrCreateTagBuilder(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED); // Generate tag so others can see it exists through JSON.

		generateDyedTags();
		generateBackwardsCompatTags();
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
	private void generateDyedTags() {
		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_BLACK)
				.add(Blocks.BLACK_BANNER).add(Blocks.BLACK_BED).add(Blocks.BLACK_CANDLE).add(Blocks.BLACK_CARPET)
				.add(Blocks.BLACK_CONCRETE).add(Blocks.BLACK_CONCRETE_POWDER).add(Blocks.BLACK_GLAZED_TERRACOTTA)
				.add(Blocks.BLACK_SHULKER_BOX).add(Blocks.BLACK_STAINED_GLASS).add(Blocks.BLACK_STAINED_GLASS_PANE)
				.add(Blocks.BLACK_TERRACOTTA).add(Blocks.BLACK_WOOL);
		
		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_BLUE)
				.add(Blocks.BLUE_BANNER).add(Blocks.BLUE_BED).add(Blocks.BLUE_CANDLE).add(Blocks.BLUE_CARPET)
				.add(Blocks.BLUE_CONCRETE).add(Blocks.BLUE_CONCRETE_POWDER).add(Blocks.BLUE_GLAZED_TERRACOTTA)
				.add(Blocks.BLUE_SHULKER_BOX).add(Blocks.BLUE_STAINED_GLASS).add(Blocks.BLUE_STAINED_GLASS_PANE)
				.add(Blocks.BLUE_TERRACOTTA).add(Blocks.BLUE_WOOL);
		
		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_BROWN)
				.add(Blocks.BROWN_BANNER).add(Blocks.BROWN_BED).add(Blocks.BROWN_CANDLE).add(Blocks.BROWN_CARPET)
				.add(Blocks.BROWN_CONCRETE).add(Blocks.BROWN_CONCRETE_POWDER).add(Blocks.BROWN_GLAZED_TERRACOTTA)
				.add(Blocks.BROWN_SHULKER_BOX).add(Blocks.BROWN_STAINED_GLASS).add(Blocks.BROWN_STAINED_GLASS_PANE)
				.add(Blocks.BROWN_TERRACOTTA).add(Blocks.BROWN_WOOL);
		
		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_CYAN)
				.add(Blocks.CYAN_BANNER).add(Blocks.CYAN_BED).add(Blocks.CYAN_CANDLE).add(Blocks.CYAN_CARPET)
				.add(Blocks.CYAN_CONCRETE).add(Blocks.CYAN_CONCRETE_POWDER).add(Blocks.CYAN_GLAZED_TERRACOTTA)
				.add(Blocks.CYAN_SHULKER_BOX).add(Blocks.CYAN_STAINED_GLASS).add(Blocks.CYAN_STAINED_GLASS_PANE)
				.add(Blocks.CYAN_TERRACOTTA).add(Blocks.CYAN_WOOL);
		
		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_GRAY)
				.add(Blocks.GRAY_BANNER).add(Blocks.GRAY_BED).add(Blocks.GRAY_CANDLE).add(Blocks.GRAY_CARPET)
				.add(Blocks.GRAY_CONCRETE).add(Blocks.GRAY_CONCRETE_POWDER).add(Blocks.GRAY_GLAZED_TERRACOTTA)
				.add(Blocks.GRAY_SHULKER_BOX).add(Blocks.GRAY_STAINED_GLASS).add(Blocks.GRAY_STAINED_GLASS_PANE)
				.add(Blocks.GRAY_TERRACOTTA).add(Blocks.GRAY_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_GREEN)
				.add(Blocks.GREEN_BANNER).add(Blocks.GREEN_BED).add(Blocks.GREEN_CANDLE).add(Blocks.GREEN_CARPET)
				.add(Blocks.GREEN_CONCRETE).add(Blocks.GREEN_CONCRETE_POWDER).add(Blocks.GREEN_GLAZED_TERRACOTTA)
				.add(Blocks.GREEN_SHULKER_BOX).add(Blocks.GREEN_STAINED_GLASS).add(Blocks.GREEN_STAINED_GLASS_PANE)
				.add(Blocks.GREEN_TERRACOTTA).add(Blocks.GREEN_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_LIGHT_BLUE)
				.add(Blocks.LIGHT_BLUE_BANNER).add(Blocks.LIGHT_BLUE_BED).add(Blocks.LIGHT_BLUE_CANDLE).add(Blocks.LIGHT_BLUE_CARPET)
				.add(Blocks.LIGHT_BLUE_CONCRETE).add(Blocks.LIGHT_BLUE_CONCRETE_POWDER).add(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA)
				.add(Blocks.LIGHT_BLUE_SHULKER_BOX).add(Blocks.LIGHT_BLUE_STAINED_GLASS).add(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE)
				.add(Blocks.LIGHT_BLUE_TERRACOTTA).add(Blocks.LIGHT_BLUE_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_LIGHT_GRAY)
				.add(Blocks.LIGHT_GRAY_BANNER).add(Blocks.LIGHT_GRAY_BED).add(Blocks.LIGHT_GRAY_CANDLE).add(Blocks.LIGHT_GRAY_CARPET)
				.add(Blocks.LIGHT_GRAY_CONCRETE).add(Blocks.LIGHT_GRAY_CONCRETE_POWDER).add(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA)
				.add(Blocks.LIGHT_GRAY_SHULKER_BOX).add(Blocks.LIGHT_GRAY_STAINED_GLASS).add(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE)
				.add(Blocks.LIGHT_GRAY_TERRACOTTA).add(Blocks.LIGHT_GRAY_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_LIME)
				.add(Blocks.LIME_BANNER).add(Blocks.LIME_BED).add(Blocks.LIME_CANDLE).add(Blocks.LIME_CARPET)
				.add(Blocks.LIME_CONCRETE).add(Blocks.LIME_CONCRETE_POWDER).add(Blocks.LIME_GLAZED_TERRACOTTA)
				.add(Blocks.LIME_SHULKER_BOX).add(Blocks.LIME_STAINED_GLASS).add(Blocks.LIME_STAINED_GLASS_PANE)
				.add(Blocks.LIME_TERRACOTTA).add(Blocks.LIME_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_MAGENTA)
				.add(Blocks.MAGENTA_BANNER).add(Blocks.MAGENTA_BED).add(Blocks.MAGENTA_CANDLE).add(Blocks.MAGENTA_CARPET)
				.add(Blocks.MAGENTA_CONCRETE).add(Blocks.MAGENTA_CONCRETE_POWDER).add(Blocks.MAGENTA_GLAZED_TERRACOTTA)
				.add(Blocks.MAGENTA_SHULKER_BOX).add(Blocks.MAGENTA_STAINED_GLASS).add(Blocks.MAGENTA_STAINED_GLASS_PANE)
				.add(Blocks.MAGENTA_TERRACOTTA).add(Blocks.MAGENTA_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_ORANGE)
				.add(Blocks.ORANGE_BANNER).add(Blocks.ORANGE_BED).add(Blocks.ORANGE_CANDLE).add(Blocks.ORANGE_CARPET)
				.add(Blocks.ORANGE_CONCRETE).add(Blocks.ORANGE_CONCRETE_POWDER).add(Blocks.ORANGE_GLAZED_TERRACOTTA)
				.add(Blocks.ORANGE_SHULKER_BOX).add(Blocks.ORANGE_STAINED_GLASS).add(Blocks.ORANGE_STAINED_GLASS_PANE)
				.add(Blocks.ORANGE_TERRACOTTA).add(Blocks.ORANGE_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_PINK)
				.add(Blocks.PINK_BANNER).add(Blocks.PINK_BED).add(Blocks.PINK_CANDLE).add(Blocks.PINK_CARPET)
				.add(Blocks.PINK_CONCRETE).add(Blocks.PINK_CONCRETE_POWDER).add(Blocks.PINK_GLAZED_TERRACOTTA)
				.add(Blocks.PINK_SHULKER_BOX).add(Blocks.PINK_STAINED_GLASS).add(Blocks.PINK_STAINED_GLASS_PANE)
				.add(Blocks.PINK_TERRACOTTA).add(Blocks.PINK_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_PURPLE)
				.add(Blocks.PURPLE_BANNER).add(Blocks.PURPLE_BED).add(Blocks.PURPLE_CANDLE).add(Blocks.PURPLE_CARPET)
				.add(Blocks.PURPLE_CONCRETE).add(Blocks.PURPLE_CONCRETE_POWDER).add(Blocks.PURPLE_GLAZED_TERRACOTTA)
				.add(Blocks.PURPLE_SHULKER_BOX).add(Blocks.PURPLE_STAINED_GLASS).add(Blocks.PURPLE_STAINED_GLASS_PANE)
				.add(Blocks.PURPLE_TERRACOTTA).add(Blocks.PURPLE_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_RED)
				.add(Blocks.RED_BANNER).add(Blocks.RED_BED).add(Blocks.RED_CANDLE).add(Blocks.RED_CARPET)
				.add(Blocks.RED_CONCRETE).add(Blocks.RED_CONCRETE_POWDER).add(Blocks.RED_GLAZED_TERRACOTTA)
				.add(Blocks.RED_SHULKER_BOX).add(Blocks.RED_STAINED_GLASS).add(Blocks.RED_STAINED_GLASS_PANE)
				.add(Blocks.RED_TERRACOTTA).add(Blocks.RED_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_WHITE)
				.add(Blocks.WHITE_BANNER).add(Blocks.WHITE_BED).add(Blocks.WHITE_CANDLE).add(Blocks.WHITE_CARPET)
				.add(Blocks.WHITE_CONCRETE).add(Blocks.WHITE_CONCRETE_POWDER).add(Blocks.WHITE_GLAZED_TERRACOTTA)
				.add(Blocks.WHITE_SHULKER_BOX).add(Blocks.WHITE_STAINED_GLASS).add(Blocks.WHITE_STAINED_GLASS_PANE)
				.add(Blocks.WHITE_TERRACOTTA).add(Blocks.WHITE_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS_YELLOW)
				.add(Blocks.YELLOW_BANNER).add(Blocks.YELLOW_BED).add(Blocks.YELLOW_CANDLE).add(Blocks.YELLOW_CARPET)
				.add(Blocks.YELLOW_CONCRETE).add(Blocks.YELLOW_CONCRETE_POWDER).add(Blocks.YELLOW_GLAZED_TERRACOTTA)
				.add(Blocks.YELLOW_SHULKER_BOX).add(Blocks.YELLOW_STAINED_GLASS).add(Blocks.YELLOW_STAINED_GLASS_PANE)
				.add(Blocks.YELLOW_TERRACOTTA).add(Blocks.YELLOW_WOOL);

		getOrCreateTagBuilder(ConventionalBlockTags.DYED_BLOCKS)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_BLACK)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_BLUE)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_BROWN)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_CYAN)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_GRAY)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_GREEN)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_LIGHT_BLUE)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_LIGHT_GRAY)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_LIME)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_MAGENTA)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_ORANGE)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_PINK)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_PURPLE)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_RED)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_WHITE)
				.addTag(ConventionalBlockTags.DYED_BLOCKS_YELLOW);
	}

	private void generateBackwardsCompatTags() {
		// Backwards compat with pre-1.21 tags. Done after so optional tag is last for better readability.
		// TODO: Remove backwards compat tag entries in 1.22

		getOrCreateTagBuilder(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED).addOptionalTag(new Identifier("c", "movement_restricted"));
		getOrCreateTagBuilder(ConventionalBlockTags.ORES_QUARTZ).addOptionalTag(new Identifier("c", "quartz_ores"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.ORES);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.BARRELS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.CHESTS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.BOOKSHELVES);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.VILLAGER_JOB_SITES);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.BUDDING_BLOCKS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.BUDS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.CLUSTERS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.SHULKER_BOXES);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.GLASS_BLOCKS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalBlockTags.GLASS_PANES);
		getOrCreateTagBuilder(ConventionalBlockTags.BARRELS_WOODEN).addOptionalTag(new Identifier("c", "wooden_barrels"));
		getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_BLOCKS).addOptionalTag(new Identifier("c", "sandstone_blocks"));
		getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_SLABS).addOptionalTag(new Identifier("c", "sandstone_slabs"));
		getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_STAIRS).addOptionalTag(new Identifier("c", "sandstone_stairs"));
		getOrCreateTagBuilder(ConventionalBlockTags.RED_SANDSTONE_BLOCKS).addOptionalTag(new Identifier("c", "red_sandstone_blocks"));
		getOrCreateTagBuilder(ConventionalBlockTags.RED_SANDSTONE_SLABS).addOptionalTag(new Identifier("c", "red_sandstone_slabs"));
		getOrCreateTagBuilder(ConventionalBlockTags.RED_SANDSTONE_STAIRS).addOptionalTag(new Identifier("c", "red_sandstone_stairs"));
		getOrCreateTagBuilder(ConventionalBlockTags.UNCOLORED_SANDSTONE_BLOCKS).addOptionalTag(new Identifier("c", "uncolored_sandstone_blocks"));
		getOrCreateTagBuilder(ConventionalBlockTags.UNCOLORED_SANDSTONE_SLABS).addOptionalTag(new Identifier("c", "uncolored_sandstone_slabs"));
		getOrCreateTagBuilder(ConventionalBlockTags.UNCOLORED_SANDSTONE_STAIRS).addOptionalTag(new Identifier("c", "uncolored_sandstone_stairs"));
	}

	private FabricTagBuilder getOrCreateTagBuilderWithOptionalLegacy(TagKey<Block> tag)
	{
		return getOrCreateTagBuilder(tag).addOptionalTag(new Identifier("c", tag.id().getPath()));
	}
}
