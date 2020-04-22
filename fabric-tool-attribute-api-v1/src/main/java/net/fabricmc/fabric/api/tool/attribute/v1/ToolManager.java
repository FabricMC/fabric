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

public final class ToolManager {
	public static Event<ToolHandler> tag(Tag<Item> tag) {
		return ToolManagerImpl.tag(tag);
	}

	public static Event<ToolHandler> general() {
		return ToolManagerImpl.general();
	}

	public static TriState handleIsEffectiveOn(ItemStack stack, BlockState state, LivingEntity user) {
		return ToolManagerImpl.handleIsEffectiveOn(stack, state, user);
	}

	public static float handleBreakingSpeed(ItemStack stack, BlockState state, LivingEntity user) {
		return ToolManagerImpl.handleBreakingSpeed(stack, state, user);
	}

	private ToolManager() {
	}

	public interface ToolHandler {
		default ActionResult isEffectiveOn(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			return ActionResult.PASS;
		}

		default Float getMiningSpeedMultiplier(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			return null;
		}
	}
}
