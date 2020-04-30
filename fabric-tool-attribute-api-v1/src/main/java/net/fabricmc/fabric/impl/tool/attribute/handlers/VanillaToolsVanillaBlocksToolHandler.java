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

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

/**
 * This handler handles items that are not a subclass of {@link DynamicAttributeTool} by
 * using the vanilla {@link Item#isEffectiveOn(BlockState)}.
 *
 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
 */
public class VanillaToolsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
	@Override
	public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool)) {
			ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

			if (entry == null) {
				return stack.getItem().isEffectiveOn(state) || stack.getItem().getMiningSpeed(stack, state) != 1.0F ? ActionResult.SUCCESS : ActionResult.PASS;
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool)) {
			float miningSpeed = stack.getItem().getMiningSpeed(stack, state);

			if (miningSpeed != 1.0F) {
				return TypedActionResult.success(miningSpeed);
			}
		}

		return TypedActionResult.pass(1.0F);
	}
}
