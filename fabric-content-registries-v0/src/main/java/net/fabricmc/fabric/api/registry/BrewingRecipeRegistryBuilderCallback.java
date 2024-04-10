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

package net.fabricmc.fabric.api.registry;

import net.minecraft.recipe.BrewingRecipeRegistry;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Use this event to register custom brewing recipes.
 */
public interface BrewingRecipeRegistryBuilderCallback {
	/**
	 * An event that is called when the brewing recipe registry is being built.
	 */
	Event<BrewingRecipeRegistryBuilderCallback> BUILD = EventFactory.createArrayBacked(BrewingRecipeRegistryBuilderCallback.class, listeners -> builder -> {
		for (BrewingRecipeRegistryBuilderCallback listener : listeners) {
			listener.build(builder);
		}
	});

	/**
	 * Called when the brewing recipe registry is being built.
	 *
	 * @param builder the {@link BrewingRecipeRegistry} instance
	 */
	void build(BrewingRecipeRegistry.class_9665 builder);
}
