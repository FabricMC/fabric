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

package net.fabricmc.fabric.impl.client.renderer.registry.item;

import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CountLabelProperties;

public class DefaultCountLabelProperties implements CountLabelProperties {
	@Override
	public boolean isVisible(ItemStack stack, String override) {
		// copied from ItemRenderer.renderGuiItemOverlay, line 327 (override was method param "countLabel")
		return override != null || stack.getCount() != 1;
	}

	@Override
	public String getContents(ItemStack stack, String override) {
		// copied from ItemRenderer.renderGuiItemOverlay, line 328 (override was method param "countLabel")
		return override == null ? Integer.toString(stack.getCount()) : override;
	}

	@Override
	public int getColor(ItemStack stack, String override) {
		// copied from ItemRenderer.renderGuiItemOverlay, line 331 (was method call param "color", converted from dec)
		return 0xFFFFFF;
	}
}
