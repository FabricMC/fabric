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

package net.fabricmc.fabric.impl.tool.attribute.handlers;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

/**
 * This handler handles items that are a subclass of {@link DynamicAttributeTool} by using the
 * vanilla {@link Item#isEffectiveOn(BlockState)} with a custom fake tool material to use the mining level
 * from {@link DynamicAttributeTool#getMiningLevel(Tag, BlockState, ItemStack, LivingEntity)}.
 *
 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
 */
public class ModdedToolsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
	private final List<Item> vanillaItems;

	public ModdedToolsVanillaBlocksToolHandler(List<Item> vanillaItems) {
		this.vanillaItems = vanillaItems;
	}

	private ToolItem getVanillaItem(int miningLevel) {
		if (miningLevel < 0) return (ToolItem) vanillaItems.get(0);
		if (miningLevel >= vanillaItems.size()) return (ToolItem) vanillaItems.get(vanillaItems.size() - 1);
		return (ToolItem) vanillaItems.get(miningLevel);
	}

	@Override
	public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (stack.getItem() instanceof DynamicAttributeTool) {
			if (ToolManagerImpl.entryNullable(state.getBlock()) != null) {
				// Block is a modded block, and we should ignore it
				return ActionResult.PASS;
			}

			// Gets the mining level from our modded tool
			int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
			if (miningLevel < 0) return ActionResult.PASS;

			ToolItem vanillaItem = getVanillaItem(miningLevel);
			return vanillaItem.isEffectiveOn(state) ? ActionResult.SUCCESS : ActionResult.PASS;
		}

		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (stack.getItem() instanceof DynamicAttributeTool) {
			// Gets the mining level from our modded tool
			int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
			if (miningLevel < 0) return null;

			float moddedToolSpeed = ((DynamicAttributeTool) stack.getItem()).getMiningSpeedMultiplier(tag, state, stack, user);
			ToolItem firstVanillaItem = getVanillaItem(miningLevel);
			ToolItem secondVanillaItem = getVanillaItem(miningLevel + 1 >= vanillaItems.size() ? vanillaItems.size() - 2 : miningLevel + 1);

			float firstSpeed = firstVanillaItem.getMiningSpeedMultiplier(new ItemStack(firstVanillaItem, 1), state);
			float secondSpeed = secondVanillaItem.getMiningSpeedMultiplier(new ItemStack(secondVanillaItem, 1), state);
			boolean hasForcedSpeed = firstSpeed == secondSpeed && firstSpeed >= 1.0F;

			// Has forced speed, which as actions like swords breaking cobwebs.
			if (hasForcedSpeed) {
				return secondSpeed != 1.0F ? TypedActionResult.success(secondSpeed) : TypedActionResult.pass(1.0F);
			}

			// We adjust the mining speed according to the ratio for the closest tool.
			float adjustedMiningSpeed = firstSpeed / firstVanillaItem.getMaterial().getMiningSpeedMultiplier() * moddedToolSpeed;
			return adjustedMiningSpeed != 1.0F ? TypedActionResult.success(adjustedMiningSpeed) : TypedActionResult.pass(1.0F);
		}

		return TypedActionResult.pass(1.0F);
	}
}
