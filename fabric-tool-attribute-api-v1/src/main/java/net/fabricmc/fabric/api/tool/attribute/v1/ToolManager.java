package net.fabricmc.fabric.api.tool.attribute.v1;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

/**
 * API facing part to register tool handlers and get information about how tools are handled.
 * Implement {@link DynamicAttributeTool} to change the mining level or speed of your tool.
 */
public final class ToolManager {
	/**
	 * Handles if the tool is effective on a block.
	 *
	 * @param state the block state to break
	 * @param stack the item stack involved with breaking the block
	 * @param user  the user involved in breaking the block, null if not applicable.
	 * @return the state of effective
	 */
	public static TriState handleIsEffectiveOn(BlockState state, ItemStack stack, LivingEntity user) {
		return ToolManagerImpl.handleIsEffectiveOn(state, stack, user);
	}

	/**
	 * Handles the breaking speed breaking a block.
	 *
	 * @param state the block state to break
	 * @param stack the item stack involved with breaking the block
	 * @param user  the user involved in breaking the block, null if not applicable.
	 * @return the speed multiplier in breaking the block, 1.0 if no change.
	 */
	public static float handleBreakingSpeed(BlockState state, ItemStack stack, LivingEntity user) {
		return ToolManagerImpl.handleBreakingSpeed(state, stack, user);
	}

	private ToolManager() {
	}
}
