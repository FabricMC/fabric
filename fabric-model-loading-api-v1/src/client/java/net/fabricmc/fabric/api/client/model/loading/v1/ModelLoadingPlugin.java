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

import java.util.Collection;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;

/**
 * A model loading plugin is used to extend the model loading process through the passed {@link Context} object.
 */
@FunctionalInterface
public interface ModelLoadingPlugin {
	/**
	 * Register a model loading plugin.
	 */
	static void register(ModelLoadingPlugin plugin) {
		ModelLoadingEventDispatcher.registerPlugin(plugin);
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
		 * Add one or multiple models (can be {@link ModelIdentifier}s) to the list of models that will be loaded.
		 */
		void addModels(Identifier... ids);

		/**
		 * Add multiple models (can be {@link ModelIdentifier}s) to the list of models that will be loaded.
		 */
		void addModels(Collection<? extends Identifier> ids);

		/**
		 * Event access to register model variant resolvers.
		 */
		Event<ModelResolver.Variant> resolveModelVariant();

		/**
		 * Event access to register model resource resolvers.
		 */
		Event<ModelResolver.Resource> resolveModelResource();

		/**
		 * Event access to monitor unbaked model loads and replace the loaded model.
		 */
		Event<ModelModifier.OnLoad> modifyModelOnLoad();

		/**
		 * Event access to swap the unbaked model used for baking without replacing the loaded model. This is important
		 * for mods which wish to wrap a model but not actually affect other models that use it as a parent (e.g.
		 * wrap a block's model into a non-{@link JsonUnbakedModel} class but still allow the item model to work fine).
		 */
		Event<ModelModifier.BeforeBake> modifyModelBeforeBake();

		/**
		 * Event access to monitor baked model loads and replace the loaded model.
		 */
		Event<ModelModifier.AfterBake> modifyModelAfterBake();
	}
}