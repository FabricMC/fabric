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

package net.fabricmc.fabric.api.client.model.loading.v1;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderInstance;

/**
 * A model loading plugin is used to extend the model loading process through the passed {@link Context} object.
 */
@FunctionalInterface
public interface ModelLoadingPlugin {
	/**
	 * Register a model loading plugin.
	 */
	static void register(ModelLoadingPlugin plugin) {
		ModelLoaderInstance.registerPlugin(plugin);
	}

	/**
	 * Called towards the beginning of the model loading process, every time resource are (re)loaded.
	 * Use the context object to extend model loading as desired.
	 */
	void onInitializeModelLoader(Context pluginContext);

	@ApiStatus.NonExtendable
	interface Context {
		/**
		 * Retrieve the current resource manager.
		 */
		ResourceManager resourceManager();

		/**
		 * Add a model (can be a {@link ModelIdentifier}) to the list of models that will be loaded.
		 */
		void addModel(Identifier identifier);

		/**
		 * Event access to register model variant providers.
		 */
		Event<ModelVariantProvider> variantProviders();

		/**
		 * Event access to register model resource providers.
		 */
		Event<ModelResourceProvider> resourceProviders();
	}
}
