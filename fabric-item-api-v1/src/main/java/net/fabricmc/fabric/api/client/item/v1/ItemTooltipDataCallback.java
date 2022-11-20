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

import java.util.List;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ItemTooltipDataCallback {
	/**
	 * Fired when {@link ItemStack#getTooltipData()} is called.
	 */
	Event<ItemTooltipDataCallback> EVENT = EventFactory.createArrayBacked(ItemTooltipDataCallback.class, callbacks -> (stack, list) -> {
		for (ItemTooltipDataCallback callback : callbacks) {
			callback.getTooltipData(stack, list);
		}
	});

	/**
	 * Called when an item stack's tooltip data is requested. Data added to {@code list}
	 * will be converted to a {@link net.minecraft.client.gui.tooltip.TooltipComponent}
	 * via {@link net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback#EVENT}
	 * before being rendered.
	 *
	 * @param stack the stack requesting tooltip data.
	 * @param list	the list containing the data's to be displayed on the stack's tooltip.
	 */
	void getTooltipData(ItemStack stack, List<TooltipData> list);
}
