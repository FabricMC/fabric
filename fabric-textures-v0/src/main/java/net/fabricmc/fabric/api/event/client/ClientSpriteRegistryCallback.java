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

import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.texture.SpriteRegistryCallbackHolder;

public interface ClientSpriteRegistryCallback {
	/**
	 * @deprecated Use the {@link ClientSpriteRegistryCallback#event(Identifier)} registration method. Since 1.14
	 * started making use of multiple sprite atlases, it is unwise to register sprites to *all* of them.
	 */
	@Deprecated
	Event<ClientSpriteRegistryCallback> EVENT = SpriteRegistryCallbackHolder.EVENT_GLOBAL;

	void registerSprites(SpriteAtlasTexture atlasTexture, Registry registry);

	/**
	 * Get an event instance for a given atlas path.
	 *
	 * @param atlasId The atlas texture ID you want to register to.
	 * @return The event for a given atlas path.
	 *
	 * @since 0.1.1
	 */
	static Event<ClientSpriteRegistryCallback> event(Identifier atlasId) {
		return SpriteRegistryCallbackHolder.eventLocal(atlasId);
	}

	/**
	 * @deprecated Use the {@link ClientSpriteRegistryCallback#event(Identifier)} registration method.
	 */
	@Deprecated
	static void registerBlockAtlas(ClientSpriteRegistryCallback callback) {
		event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register(callback);
	}

	class Registry {
		private final Map<Identifier, Sprite> spriteMap;
		private final Consumer<Identifier> defaultSpriteRegister;

		public Registry(Map<Identifier, Sprite> spriteMap, Consumer<Identifier> defaultSpriteRegister) {
			this.spriteMap = spriteMap;
			this.defaultSpriteRegister = defaultSpriteRegister;
		}

		/**
		 * Register a sprite to be loaded using the default implementation.
		 *
		 * @param id The sprite identifier.
		 */
		public void register(Identifier id) {
			this.defaultSpriteRegister.accept(id);
		}

		/**
		 * Register a custom sprite to be added and loaded.
		 *
		 * @param sprite The sprite to be added.
		 */
		public void register(Sprite sprite) {
			spriteMap.put(sprite.getId(), sprite);
		}
	}
}
