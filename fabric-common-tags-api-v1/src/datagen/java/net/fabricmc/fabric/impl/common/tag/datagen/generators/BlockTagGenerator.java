package net.fabricmc.fabric.impl.common.tag.datagen.generators;

import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tags.v1.CommonBlockTags;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
	public BlockTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(CommonBlockTags.IRON_ORES)
				.addOptionalTag(BlockTags.IRON_ORES);
		getOrCreateTagBuilder(CommonBlockTags.NETHERITE_ORES)
				.add(Blocks.ANCIENT_DEBRIS);
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
				.addOptionalTag(CommonBlockTags.NETHERITE_ORES);
		getOrCreateTagBuilder(CommonBlockTags.CHESTS)
				.add(Blocks.CHEST)
				.add(Blocks.ENDER_CHEST)
				.add(Blocks.TRAPPED_CHEST);
	}
}
