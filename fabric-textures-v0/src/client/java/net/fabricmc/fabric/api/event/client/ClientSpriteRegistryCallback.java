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
import java.util.function.BiConsumer;

import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.texture.SpriteRegistryCallbackHolder;

public interface ClientSpriteRegistryCallback {
	/**
	 * Add sprites to the map of sprites that will be baked into the sprite atlas.
	 *
	 * @see SpriteLoader#addResources(ResourceManager, String, BiConsumer) For adding textures from a folder recursively.
	 * @see SpriteLoader#addResource(ResourceManager, Identifier, BiConsumer) For adding a single texture.
	 */
	void registerSprites(ResourceManager resourceManager, Map<Identifier, Resource> sprites);

	/**
	 * Get an event instance for a given atlas path.
	 *
	 * @param atlasId The atlas texture ID you want to register to.
	 * @return The event for a given atlas path.
	 * @since 0.1.1
	 *
	 * @see PlayerScreenHandler#BLOCK_ATLAS_TEXTURE
	 */
	static Event<ClientSpriteRegistryCallback> event(Identifier atlasId) {
		return SpriteRegistryCallbackHolder.eventLocal(atlasId);
	}
}
