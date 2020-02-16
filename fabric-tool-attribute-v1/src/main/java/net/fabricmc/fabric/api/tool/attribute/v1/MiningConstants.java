package net.fabricmc.fabric.api.tool.attribute.v1;

/**
 * Constants for all the mining levels and speed multipliers of vanilla materials.
 */
public final class MiningConstants {
	private MiningConstants() { }

	public static final int WOOD_MINING_LEVEL = 0;
	public static final int STONE_MINING_LEVEL = 1;
	public static final int IRON_MINING_LEVEL = 2;
	public static final int DIAMOND_MINING_LEVEL = 3;
	public static final int NETHERITE_MINING_LEVEL = 3;
	public static final int GOLD_MINING_LEVEL = 0;

	public static final float WOOD_MINING_SPEED_MULTIPLIER = 2;
	public static final float STONE_MINING_SPEED_MULTIPLIER = 4;
	public static final float IRON_MINING_SPEED_MULTIPLIER = 6;
	public static final float DIAMOND_MINING_SPEED_MULTIPLIER = 8;
	public static final float NETHERITE_MINING_SPEED_MULTIPLIER = 9;
	public static final float GOLD_MINING_SPEED_MULTIPLIER = 12;
}
