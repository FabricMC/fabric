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

package net.fabricmc.fabric.api.client.rendering.v1;

import java.util.List;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Allows registering custom {@link TooltipData} object for item.
 * This allows you to add your own tooltips to existing items.
 *
 * <p>Custom {@link TooltipData} should be registered using {@link TooltipComponentCallback},
 * otherwise game will crash when trying to map {@link TooltipData} to {@link TooltipComponent}.
 */
@FunctionalInterface
public interface TooltipDataCallback {
	Event<TooltipDataCallback> EVENT = EventFactory.createArrayBacked(TooltipDataCallback.class, callbacks -> (itemStack, tooltipDataList) -> {
		for (TooltipDataCallback callback : callbacks) {
			callback.appendTooltipData(itemStack, tooltipDataList);
		}
	});

	/**
	 * Add your own {@link TooltipData} to passed list if itemStack matches your requirements
	 */
	void appendTooltipData(ItemStack itemStack, List<TooltipData> tooltipDataList);
}
