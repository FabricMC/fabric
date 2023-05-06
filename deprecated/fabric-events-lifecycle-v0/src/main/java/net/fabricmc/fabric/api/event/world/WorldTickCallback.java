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

package net.fabricmc.fabric.api.event.world;

import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@Deprecated
public interface WorldTickCallback {
	/**
	 * @deprecated The new WorldTickCallback has been split into a client and server callback.
	 * Please use the {@link ServerTickEvents#END_WORLD_TICK server} or {@link net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents#END_WORLD_TICK client} callbacks.
	 */
	@Deprecated
	Event<WorldTickCallback> EVENT = EventFactory.createArrayBacked(WorldTickCallback.class,
			(listeners) -> {
				return (world) -> {
					for (WorldTickCallback event : listeners) {
						event.tick(world);
					}
				};
			}
	);

	void tick(World world);
}
