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

package net.fabricmc.fabric.client.texture;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Helper class for registering Sprites during loading.
 */
public class SpriteRegistry {
	private final Map<Identifier, Sprite> spriteMap;
	private final Consumer<Identifier> defaultSpriteRegister;

	public SpriteRegistry(Map<Identifier, Sprite> spriteMap, Consumer<Identifier> defaultSpriteRegister) {
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
