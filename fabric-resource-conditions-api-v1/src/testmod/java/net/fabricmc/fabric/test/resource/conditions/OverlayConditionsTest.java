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

package net.fabricmc.fabric.test.resource.conditions;

import java.util.List;
import java.util.Optional;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class OverlayConditionsTest implements ModInitializer {
	private static final String MOD_ID = "fabric-resource-conditions-api-v1-testmod";

	private static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(MOD_ID);

		if (container.isEmpty() || !ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(MOD_ID, "overlay_test"), container.get(), ResourcePackActivationType.DEFAULT_ENABLED)) {
			throw new AssertionError("Could not register overlay_test datapack.");
		}

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			List<Identifier> recipes = server.getRecipeManager().keys().toList();

			if (recipes.contains(id("dont_overlay"))) {
				throw new AssertionError("dont_overlay recipe should not have been overlayed.");
			}

			if (!recipes.contains(id("do_overlay"))) {
				throw new AssertionError("do_overlay recipe should have been overlayed.");
			}
		});
	}
}
