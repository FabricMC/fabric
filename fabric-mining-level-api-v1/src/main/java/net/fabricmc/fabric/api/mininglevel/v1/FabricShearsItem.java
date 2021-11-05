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

package net.fabricmc.fabric.api.mininglevel.v1;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.Tag;

/**
 * A version of {@code ShearsItem} which takes a {@code ToolMaterial}.
 */
public class FabricShearsItem extends ShearsItem implements FabricTool {
	private final ToolMaterial material;

	public FabricShearsItem(ToolMaterial material, Item.Settings settings) {
		super(settings.maxDamageIfAbsent(material.getDurability()));
		this.material = material;
	}

	@Override
	public ToolMaterial getToolMaterial() {
		return material;
	}

	@Override
	public Tag<Block> getEffectiveBlocks() {
		return FabricMineableTags.SHEARS_MINEABLE;
	}
}
