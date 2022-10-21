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

package net.fabricmc.fabric.impl.client.texture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;

public final class SpriteRegistryCallbackHolder {
	private static final Map<Identifier, Event<ClientSpriteRegistryCallback>> eventMap = new ConcurrentHashMap<>();

	private SpriteRegistryCallbackHolder() { }

	public static Event<ClientSpriteRegistryCallback> eventLocal(Identifier key) {
		return eventMap.computeIfAbsent(key, (a) -> createEvent());
	}

	private static Event<ClientSpriteRegistryCallback> createEvent() {
		return EventFactory.createArrayBacked(ClientSpriteRegistryCallback.class,
			(listeners) -> (atlasTexture, registry) -> {
				for (ClientSpriteRegistryCallback callback : listeners) {
					callback.registerSprites(atlasTexture, registry);
				}
			}
		);
	}
}
