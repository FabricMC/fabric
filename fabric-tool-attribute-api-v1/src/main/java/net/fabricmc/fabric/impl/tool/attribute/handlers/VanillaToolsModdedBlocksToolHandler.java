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
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

/**
 * This handler handles items that are not a subclass of {@link DynamicAttributeTool} by
 * comparing their mining level using {@link ToolMaterial#getMiningLevel()} and the block mining level.
 *
 * <p>Only applicable to modded blocks that are registered, as only they have the registered required mining level.</p>
 */
public class VanillaToolsModdedBlocksToolHandler implements ToolManagerImpl.ToolHandler {
	@NotNull
	@Override
	public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool)) {
			ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

			if (entry != null) {
				int miningLevel = stack.getItem() instanceof ToolItem ? ((ToolItem) stack.getItem()).getMaterial().getMiningLevel() : -1;
				int requiredMiningLevel = entry.getMiningLevel(tag);
				return requiredMiningLevel >= 0 && miningLevel >= 0 && miningLevel >= requiredMiningLevel ? ActionResult.SUCCESS : ActionResult.PASS;
			}
		}

		return ActionResult.PASS;
	}

	@NotNull
	@Override
	public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool)) {
			ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

			if (entry != null && entry.getMiningLevel(tag) >= 0) {
				float multiplier = stack.getItem() instanceof ToolItem ? ((ToolItem) stack.getItem()).getMaterial().getMiningSpeedMultiplier() : stack.getItem().getMiningSpeedMultiplier(stack, state);
				if (multiplier != 1.0F) return TypedActionResult.success(multiplier);
			}
		}

		return TypedActionResult.pass(1.0F);
	}
}
