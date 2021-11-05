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

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import net.fabricmc.fabric.api.mininglevel.v1.FabricTool;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

/**
 * This handler handles items that are registered in the {@link FabricToolTags#SHEARS} by using the
 * vanilla {@link Item#isSuitableFor(BlockState)} using the vanilla shears or the item itself if the item
 * is a subclass of {@link ShearsItem}.
 *
 * <p>Only applicable to items that are not a subclass of {@link DynamicAttributeTool}</p>
 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
 */
public class ShearsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
	private static final Item VANILLA_ITEM = Items.SHEARS;

	@NotNull
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

			FabricTool vanillaItem = (FabricTool) VANILLA_ITEM;
			return vanillaItem.isSuitableFor(miningLevel, state) ? ActionResult.SUCCESS : ActionResult.PASS;
		}

		return ActionResult.PASS;
	}

	@NotNull
	@Override
	public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		float speed = 1.0F;

		if (!(stack.getItem() instanceof DynamicAttributeTool)) {
			if (!(stack.getItem() instanceof ShearsItem)) {
				speed = VANILLA_ITEM.getMiningSpeedMultiplier(new ItemStack(VANILLA_ITEM), state);
			} else {
				speed = stack.getItem().getMiningSpeedMultiplier(stack, state);
			}
		}

		return speed != 1.0F ? TypedActionResult.success(speed) : TypedActionResult.pass(1.0F);
	}
}
