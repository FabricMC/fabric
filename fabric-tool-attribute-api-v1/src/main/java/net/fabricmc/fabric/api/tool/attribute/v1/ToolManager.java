package net.fabricmc.fabric.api.tool.attribute.v1;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

/**
 * API facing part to register tool handlers and get information about how tools are handled.
 */
public final class ToolManager {
	/**
	 * Returns a event for the tag provided, creates a new event if it does not exist.
	 *
	 * @param tag the tag provided for the tool
	 * @return the event callback.
	 */
	public static Event<ToolHandler> tag(Tag<Item> tag) {
		return ToolManagerImpl.tag(tag);
	}

	/**
	 * Returns a event used for every tag registered.
	 *
	 * @return the event callback.
	 */
	public static Event<ToolHandler> general() {
		return ToolManagerImpl.general();
	}

	/**
	 * Handles if the tool is effective on a block.
	 *
	 * @param stack the item stack involved with breaking the block
	 * @param state the block state to break
	 * @param user  the user involved in breaking the block, null if not applicable.
	 * @return the state of effective
	 */
	public static TriState handleIsEffectiveOn(ItemStack stack, BlockState state, LivingEntity user) {
		return ToolManagerImpl.handleIsEffectiveOn(stack, state, user);
	}

	/**
	 * Handles the breaking speed breaking a block.
	 *
	 * @param stack the item stack involved with breaking the block
	 * @param state the block state to break
	 * @param user  the user involved in breaking the block, null if not applicable.
	 * @return the speed multiplier in breaking the block, 1.0 if no change.
	 */
	public static float handleBreakingSpeed(ItemStack stack, BlockState state, LivingEntity user) {
		return ToolManagerImpl.handleBreakingSpeed(stack, state, user);
	}

	private ToolManager() {
	}

	/**
	 * The handler to handle tool speed and effectiveness.
	 *
	 * @see net.fabricmc.fabric.impl.tool.attribute.ToolHandlers for default handlers.
	 */
	public interface ToolHandler {
		/**
		 * Determines whether this handler is active and effective of the tools.
		 *
		 * @param tag   the tag involved
		 * @param stack the item stack breaking the block
		 * @param user  the user involved in breaking the block, null if not applicable.
		 * @param state the block state to break
		 * @return the result of effectiveness
		 */
		default ActionResult isEffectiveOn(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			return ActionResult.PASS;
		}

		/**
		 * Determines the mining speed multiplier of the tools.
		 *
		 * @param tag   the tag involved
		 * @param stack the item stack breaking the block
		 * @param user  the user involved in breaking the block, null if not applicable.
		 * @param state the block state to break
		 * @return the result of mining speed, null if pass.
		 */
		default Float getMiningSpeedMultiplier(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			return null;
		}
	}
}
