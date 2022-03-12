package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.common.tag.TagRegistration;

public class CommonBlockTags {
	public static final TagKey<Block> IRON_ORES = register("iron_ores");
	public static final TagKey<Block> GOLD_ORES = register("gold_ores");
	public static final TagKey<Block> REDSTONE_ORES = register("redstone_ores");
	public static final TagKey<Block> COPPER_ORES = register("copper_ores");
	public static final TagKey<Block> ORES = register("ores");
	public static final TagKey<Block> NETHERITE_ORES = register("netherite_ores");
	public static final TagKey<Block> CROPS = register("crops");
	public static final TagKey<Block> CHESTS = register("chests");

	private static TagKey<Block> register(String tagID) {
		return TagRegistration.BLOCK_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Block> registerFabric(String tagID) {
		return TagRegistration.BLOCK_TAG_REGISTRATION.registerFabric(tagID);
	}
}
