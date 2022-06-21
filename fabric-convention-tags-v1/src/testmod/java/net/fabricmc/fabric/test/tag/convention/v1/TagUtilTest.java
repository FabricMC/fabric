package net.fabricmc.fabric.test.tag.convention.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEnchantmentTags;
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil;

public class TagUtilTest implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagUtilTest.class);

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if (!TagUtil.isIn(ConventionalEnchantmentTags.INCREASES_BLOCK_DROPS, Enchantments.FORTUNE)) {
				throw new AssertionError("Failed to find fortune in c:fortune!");
			}

			// If this fails, the tag is missing a biome or the util is broken
			if (!TagUtil.isIn(ConventionalBiomeTags.IN_OVERWORLD, server.getOverworld().getBiome(BlockPos.ORIGIN))) {
				throw new AssertionError("Failed to find an overworld biome (%s) in c:in_overworld!".formatted(server.getOverworld().getBiome(BlockPos.ORIGIN)));
			}

			if (!TagUtil.isIn(server.getRegistryManager(), ConventionalBlockTags.ORES, Blocks.DIAMOND_ORE)) {
				throw new AssertionError("Failed to find diamond ore in c:ores!");
			}

			//Success!
			LOGGER.info("Completed TagUtil tests!");
		});
	}
}
