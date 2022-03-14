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

package net.fabricmc.fabric.impl.tag.common.datagen.generators;

import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.v1.CommonBlockTags;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
	public BlockTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(CommonBlockTags.IRON_ORES)
				.addOptionalTag(BlockTags.IRON_ORES);
		getOrCreateTagBuilder(CommonBlockTags.NETHERITE_ORES);
		getOrCreateTagBuilder(CommonBlockTags.GOLD_ORES)
				.addOptionalTag(BlockTags.GOLD_ORES);
		getOrCreateTagBuilder(CommonBlockTags.COPPER_ORES)
				.addOptionalTag(BlockTags.COPPER_ORES);
		getOrCreateTagBuilder(CommonBlockTags.REDSTONE_ORES)
				.addOptionalTag(BlockTags.REDSTONE_ORES);
		getOrCreateTagBuilder(CommonBlockTags.ORES)
				.addOptionalTag(CommonBlockTags.REDSTONE_ORES)
				.addOptionalTag(CommonBlockTags.COPPER_ORES)
				.addOptionalTag(CommonBlockTags.GOLD_ORES)
				.addOptionalTag(CommonBlockTags.IRON_ORES)
				.addOptionalTag(BlockTags.COAL_ORES)
				.addOptionalTag(BlockTags.EMERALD_ORES)
				.addOptionalTag(BlockTags.LAPIS_ORES)
				.addOptionalTag(BlockTags.DIAMOND_ORES)
				.add(Blocks.NETHER_QUARTZ_ORE)
				.addOptionalTag(CommonBlockTags.NETHERITE_ORES);
		getOrCreateTagBuilder(CommonBlockTags.CHESTS)
				.add(Blocks.CHEST)
				.add(Blocks.ENDER_CHEST)
				.add(Blocks.TRAPPED_CHEST);
		getOrCreateTagBuilder(CommonBlockTags.BOOKSHELVES)
				.add(Blocks.BOOKSHELF);
	}
}
