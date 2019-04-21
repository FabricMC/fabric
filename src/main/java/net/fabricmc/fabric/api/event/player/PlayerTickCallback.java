/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.profiler.Profiler;

/**
 * Callback for ticking a player. Useful for updating effects given by a player's equipped items.
 */
public interface PlayerTickCallback {
	public static final Event<PlayerTickCallback> EVENT = EventFactory.createArrayBacked(PlayerTickCallback.class,
			(listeners) -> {
				if (EventFactory.isProfilingEnabled()) {
					return (player) -> {
						Profiler profiler = player.getServer().getProfiler();
						profiler.push("fabricPlayerTick");
						for (PlayerTickCallback event : listeners) {
							profiler.push(EventFactory.getHandlerName(event));
							event.tick(player);
							profiler.pop();
						}
						profiler.pop();
					};
				} else {
					return (player) -> {
						for (PlayerTickCallback event : listeners) {
							event.tick(player);
						}
					};
				}
			}
	);

	void tick(PlayerEntity player);
}
