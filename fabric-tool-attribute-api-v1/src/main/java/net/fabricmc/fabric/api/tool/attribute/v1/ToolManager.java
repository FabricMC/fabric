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
