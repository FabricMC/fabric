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

import net.minecraft.block.Block;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface BlockConstructedCallback {
	Event<BlockConstructedCallback> EVENT = EventFactory.createArrayBacked(BlockConstructedCallback.class,
			(listeners) -> (settings, builtBlock) -> {
				for (BlockConstructedCallback callback : listeners) {
					callback.building(settings, builtBlock);
				}
			}
	);

	void building(Block.Settings settings, Block builtBlock);
}
