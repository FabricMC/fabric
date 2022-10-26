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

package net.fabricmc.fabric.mixin.client.texture;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.texture.SpriteRegistryCallbackHolder;

/**
 * Mix in to where texture atlases discover the files they should bake.
 */
@Mixin(SpriteAtlasManager.class)
public class SpriteAtlasManagerMixin {
	@ModifyVariable(method = "<init>", at = @At("HEAD"))
	private static Map<Identifier, SpriteAtlasManager.SpriteResourceLoader> initAtlases(Map<Identifier, SpriteAtlasManager.SpriteResourceLoader> atlases) {
		// Make modifiable
		atlases = new HashMap<>(atlases);

		for (Map.Entry<Identifier, SpriteAtlasManager.SpriteResourceLoader> entry : atlases.entrySet()) {
			SpriteAtlasManager.SpriteResourceLoader resourceFinder = entry.getValue();

			entry.setValue(resourceManager -> {
				// First run vanilla logic
				Map<Identifier, Resource> resources = resourceFinder.apply(resourceManager);

				// Then invoke event
				SpriteRegistryCallbackHolder
						.eventLocal(entry.getKey())
						.invoker()
						.registerSprites(resourceManager, resources);

				return resources;
			});
		}

		return atlases;
	}
}
