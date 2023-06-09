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

package net.fabricmc.fabric.impl.resource.loader.reloader;

import java.util.Locale;

import net.minecraft.loot.LootManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderKeys;

public class ServerReloaderSorting extends ReloaderSorting {
	@Override
	protected Identifier getVanillaReloaderId(ResourceReloader reloader) {
		if (reloader instanceof TagManagerLoader) {
			return ResourceReloaderKeys.TAGS;
		} else if (reloader instanceof RecipeManager) {
			return ResourceReloaderKeys.RECIPES;
		} else if (reloader instanceof ServerAdvancementLoader) {
			return ResourceReloaderKeys.ADVANCEMENTS;
		} else if (reloader instanceof FunctionLoader) {
			return ResourceReloaderKeys.FUNCTIONS;
		} else if (reloader instanceof LootManager) {
			return ResourceReloaderKeys.LOOT_TABLES;
		} else {
			// Cannot be accessed by mods, but we still need an id for reloader sorting.
			return new Identifier("minecraft", "private/" + reloader.getClass().getSimpleName().toLowerCase(Locale.ROOT));
		}
	}
}
