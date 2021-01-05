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

package net.fabricmc.fabric.api.client.item.v1;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public interface ItemTooltipComponentCallback {
	/**
	 * Fired when the game is creating a {@link TooltipContext}.
	 */
	Event<ItemTooltipComponentCallback> EVENT = EventFactory.createArrayBacked(ItemTooltipComponentCallback.class, callbacks -> (tooltipData) -> {
		for (ItemTooltipComponentCallback callback : callbacks) {
			TooltipComponent component = callback.createTooltipComponent(tooltipData);

			if (component != null) {
				return component;
			}
		}

		return null;
	});

	/**
	 * Called when the {@link TooltipComponent} for an {@link ItemStack} is being created.
	 *
	 * @param tooltipData The data provided for the tooltip
	 *
	 * @return A {@link TooltipComponent} or null to continue
	 */
	TooltipComponent createTooltipComponent(TooltipData tooltipData);
}
