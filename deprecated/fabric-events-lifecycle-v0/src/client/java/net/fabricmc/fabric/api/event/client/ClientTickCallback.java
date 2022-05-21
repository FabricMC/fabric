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

package net.fabricmc.fabric.api.event.client;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Deprecated
public interface ClientTickCallback {
	/**
	 * @deprecated Please use {@link ClientTickEvents#END_CLIENT_TICK}.
	 */
	@Deprecated
	Event<ClientTickCallback> EVENT = EventFactory.createArrayBacked(ClientTickCallback.class,
			(listeners) -> {
				if (EventFactory.isProfilingEnabled()) {
					return (client) -> {
						client.getProfiler().push("fabricClientTick");

						for (ClientTickCallback event : listeners) {
							client.getProfiler().push(EventFactory.getHandlerName(event));
							event.tick(client);
							client.getProfiler().pop();
						}

						client.getProfiler().pop();
					};
				} else {
					return (client) -> {
						for (ClientTickCallback event : listeners) {
							event.tick(client);
						}
					};
				}
			}
	);

	void tick(MinecraftClient client);
}
