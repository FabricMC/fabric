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

import net.minecraft.block.Block;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;

/**
 * A model loading plugin is used to extend the model loading process through the passed {@link Context} object.
 *
 * <p>{@link PreparableModelLoadingPlugin} can be used if some resources need to be loaded from the
 * {@link ResourceManager}.
 */
@FunctionalInterface
public interface ModelLoadingPlugin {
	/**
	 * Registers a model loading plugin.
	 */
	static void register(ModelLoadingPlugin plugin) {
		ModelLoadingPluginManager.registerPlugin(plugin);
	}

	/**
	 * Called towards the beginning of the model loading process, every time resource are (re)loaded.
	 * Use the context object to extend model loading as desired.
	 */
	void onInitializeModelLoader(Context pluginContext);

	@ApiStatus.NonExtendable
	interface Context {
		/**
		 * Adds one or more models that will be loaded, baked, and made available through
		 * {@link FabricBakedModelManager#getModel(Identifier)}.
		 */
		void addModels(Identifier... ids);

		/**
		 * Adds multiple models that will be loaded, baked, and made available through
		 * {@link FabricBakedModelManager#getModel(Identifier)}.
		 */
		void addModels(Collection<? extends Identifier> ids);

		/**
		 * Registers a block state resolver for a block.
		 *
		 * <p>The block must be registered and a block state resolver must not have been previously registered for the
		 * block.
		 */
		void registerBlockStateResolver(Block block, BlockStateResolver resolver);

		/**
		 * Event access to register model resolvers.
		 */
		Event<ModelResolver> resolveModel();

		/**
		 * Event access to monitor unbaked model loads and replace the loaded model.
		 */
		Event<ModelModifier.OnLoad> modifyModelOnLoad();

		/**
		 * Event access to replace the unbaked model used for baking without replacing the cached model.
		 *
		 * <p>This is useful for mods which wish to wrap a model without affecting other models that use it as a parent
		 * (e.g. wrap a block's model into a non-{@link JsonUnbakedModel} class but still allow the item model to be
		 * loaded and baked without exceptions).
		 */
		Event<ModelModifier.BeforeBake> modifyModelBeforeBake();

		/**
		 * Event access to monitor baked model loads and replace the loaded model.
		 */
		Event<ModelModifier.AfterBake> modifyModelAfterBake();
	}
}
