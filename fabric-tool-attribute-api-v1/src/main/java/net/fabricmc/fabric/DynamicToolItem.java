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

package net.fabricmc.fabric;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolLevel;

public class DynamicToolItem extends Item implements DynamicAttributeTool {
	private final Tag<Item> toolType;
	private final ToolLevel miningLevel;
	private final float toolSpeed;

	// The durability of the item can be applied via Settings#maxDamage
	private DynamicToolItem(Settings settings, Tag<Item> toolType, ToolLevel miningLevel, float toolSpeed) {
		super(settings);
		this.toolType = toolType;
		this.miningLevel = miningLevel;
		this.toolSpeed = toolSpeed;
	}

	@Override
	public float getToolMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (tag.equals(toolType)) {
			return this.miningLevel.getLevel();
		}

		return ToolLevel.NONE.getLevel();
	}

	@Override
	public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (tag.equals(toolType)) {
			return toolSpeed;
		}

		return 1.0F;
	}
}
