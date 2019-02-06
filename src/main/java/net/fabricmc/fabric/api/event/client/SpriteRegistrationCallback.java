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

package net.fabricmc.fabric.api.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Consumer;

public interface SpriteRegistrationCallback {
	public static final Event<SpriteRegistrationCallback> EVENT = EventFactory.arrayBacked(SpriteRegistrationCallback.class,
		(listeners) -> (atlasTexture, registry) -> {
			for (SpriteRegistrationCallback callback : listeners) {
				callback.registerSprites(atlasTexture, registry);
			}
		}
	);

	void registerSprites(SpriteAtlasTexture atlasTexture, Registry registry);

	static void registerBlockAtlas(SpriteRegistrationCallback callback) {
		EVENT.register((atlasTexture, registry) -> {
			if (atlasTexture == MinecraftClient.getInstance().getSpriteAtlas()) {
				callback.registerSprites(atlasTexture, registry);
			}
		});
	}

	public static class Registry {
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
			this.spriteMap.put(sprite.getId(), sprite);
		}
	}
}
