package net.fabricmc.fabric.api.enchantment;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * To be used on blocks which increase the power of an enchanting table.
 */
public interface EnchantingPowerProvider {

	/**
	 * Returns the amount of how much to increase the enchanting power
	 * @param world The world of the block
	 * @param blockPos The block position of the block
	 * @return The power amount provided by this block
	 */
	int getEnchantingPower(World world, BlockPos blockPos);
}
