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

package net.fabricmc.fabric.mixin.mininglevel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;

import net.fabricmc.fabric.api.mininglevel.v1.FabricTool;

@Mixin(ToolItem.class)
abstract class ToolItemMixin implements FabricTool {
	@Shadow
	public abstract ToolMaterial getMaterial();

	@Override
	public ToolMaterial getToolMaterial() {
		return getMaterial(); // Redirect FabricToolItem method to vanilla method
	}
}
