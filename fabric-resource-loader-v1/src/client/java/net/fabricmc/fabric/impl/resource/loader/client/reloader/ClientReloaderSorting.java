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

package net.fabricmc.fabric.impl.resource.loader.client.reloader;

import java.util.Locale;

import net.minecraft.client.font.FontManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderKeys;
import net.fabricmc.fabric.impl.resource.loader.reloader.ReloaderSorting;

public class ClientReloaderSorting extends ReloaderSorting {
	@Override
	protected Identifier getVanillaReloaderId(ResourceReloader reloader) {
		if (reloader instanceof LanguageManager) {
			return ResourceReloaderKeys.LANGUAGES;
		} else if (reloader instanceof TextureManager) {
			return ResourceReloaderKeys.TEXTURES;
		} else if (reloader instanceof SoundManager) {
			return ResourceReloaderKeys.SOUNDS;
		} else if (reloader instanceof FontManager) {
			return ResourceReloaderKeys.FONTS;
		} else if (reloader instanceof BakedModelManager) {
			return ResourceReloaderKeys.BAKED_MODELS;
		} else if (reloader instanceof EntityRenderDispatcher) {
			return ResourceReloaderKeys.ENTITY_RENDER_DISPATCHER;
		} else {
			// Cannot be accessed by mods, but we still need an id for reloader sorting.
			return new Identifier("minecraft", "private/" + reloader.getClass().getSimpleName().toLowerCase(Locale.ROOT));
		}
	}
}
