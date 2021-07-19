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
import org.jetbrains.annotations.Nullable;

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
 * This handler handles items that are registered in a tool tag,
 * but aren't any known tool items in code. For that reason, we use a few callback values:
 * The mining level of this kind of item is always 0, and the mining speed multiplier for matching
 * blocks is {@link #GENERIC_FASTER_MINING_SPEED}.
 *
 * <p>Only applicable to items that are not a subclass of {@link DynamicAttributeTool} or {@link ToolItem}</p>
 */
public class TaggedToolsModdedBlocksToolHandler implements ToolManagerImpl.ToolHandler {
	/**
	 * A generic "faster" mining speed multiplier. This is the speed at which shears break wool,
	 * which was picked as an arbitrary choice for tools that just don't provide any more details
	 * about themselves via code.
	 *
	 * @see net.fabricmc.fabric.mixin.mininglevel.ShearsItemMixin
	 */
	static final float GENERIC_FASTER_MINING_SPEED = 5.0f;

	@NotNull
	@Override
	public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool) && !(stack.getItem() instanceof ToolItem)) {
			@Nullable ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

			if (entry != null) {
				int miningLevel = 0; // minimum mining level: the tool is tagged but nothing else is said about it
				int requiredMiningLevel = entry.getMiningLevel(tag);
				return requiredMiningLevel >= miningLevel ? ActionResult.SUCCESS : ActionResult.PASS;
			}
		}

		return ActionResult.PASS;
	}

	@NotNull
	@Override
	public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool) && !(stack.getItem() instanceof ToolItem)) {
			@Nullable ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

			if (entry != null && entry.getMiningLevel(tag) >= 0) {
				return TypedActionResult.success(GENERIC_FASTER_MINING_SPEED);
			}
		}

		return TypedActionResult.pass(1.0F);
	}
}
