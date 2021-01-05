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

package net.fabricmc.fabric.mixin.item.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipComponentCallback;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {
	/**
	 * @author gudenau
	 * @reason Mixins can't inject into interfaces
	 */
	@Overwrite
	static TooltipComponent of(TooltipData data) {
		TooltipComponent component = ItemTooltipComponentCallback.EVENT.invoker().createTooltipComponent(data);

		if (component != null) {
			return component;
		} else if (data instanceof BundleTooltipData) {
			return new BundleTooltipComponent((BundleTooltipData) data);
		} else {
			throw new IllegalArgumentException("Unknown TooltipComponent");
		}
	}
}
