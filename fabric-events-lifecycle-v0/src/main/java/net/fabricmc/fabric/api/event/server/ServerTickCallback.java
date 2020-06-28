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

package net.fabricmc.fabric.api.event.server;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@Deprecated
public interface ServerTickCallback {
	/**
	 * @deprecated Please use {@link ServerTickEvents#END_SERVER_TICK}
	 */
	@Deprecated
	Event<ServerTickCallback> EVENT = EventFactory.createArrayBacked(ServerTickCallback.class,
			(listeners) -> {
				if (EventFactory.isProfilingEnabled()) {
					return (server) -> {
						server.getProfiler().push("fabricServerTick");

						for (ServerTickCallback event : listeners) {
							server.getProfiler().push(EventFactory.getHandlerName(event));
							event.tick(server);
							server.getProfiler().pop();
						}

						server.getProfiler().pop();
					};
				} else {
					return (server) -> {
						for (ServerTickCallback event : listeners) {
							event.tick(server);
						}
					};
				}
			}
	);

	void tick(MinecraftServer server);
}
