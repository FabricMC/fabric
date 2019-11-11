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

package net.fabricmc.fabric.api.event.registry;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ItemConstructedCallback {
	Event<ItemConstructedCallback> EVENT = EventFactory.createArrayBacked(ItemConstructedCallback.class,
			(listeners) -> (settings, builtItem) -> {
				for (ItemConstructedCallback callback : listeners) {
					callback.building(settings, builtItem);
				}
			}
	);

	void building(Item.Settings settings, Item builtItem);
}
