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

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

/**
 * This handler handles items that are registered in a tool tag,
 * but aren't any known tool items in code. For that reason, we use a few callback values:
 * The mining level of this kind of item is always 0, and the mining speed multiplier is always 1.
 *
 * <p>Only applicable to items that are not a subclass of {@link DynamicAttributeTool} or {@link ToolItem}</p>
 */
public class TaggedToolsModdedBlocksToolHandler implements ToolManagerImpl.ToolHandler {
	@NotNull
	@Override
	public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool) && !(stack.getItem() instanceof ToolItem)) {
			@Nullable ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

			if (entry != null) {
				int requiredMiningLevel = entry.getMiningLevel(tag);
				// (requiredMiningLevel == 0) is equivalent to
				// (requiredMiningLevel >= 0 && toolMiningLevel >= requiredMiningLevel), which is used in other handlers.
				// Since the tool mining level of these is always 0 (in the absence of better info), the condition
				// simplifies to (== 0). The compiler couldn't optimise the other one... :(
				return requiredMiningLevel == 0 ? ActionResult.SUCCESS : ActionResult.PASS;
			}
		}

		return ActionResult.PASS;
	}
}
