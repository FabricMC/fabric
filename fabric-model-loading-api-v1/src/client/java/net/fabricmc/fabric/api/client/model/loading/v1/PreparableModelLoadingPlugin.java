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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import net.minecraft.resource.ResourceManager;

import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;

/**
 * A model loading plugin is used to extend the model loading process through the passed {@link ModelLoadingPlugin.Context} object.
 *
 * <p>This version of {@link ModelLoadingPlugin} allows loading some data off-thread before the model loading process starts.
 */
@FunctionalInterface
public interface PreparableModelLoadingPlugin<T> {
	/**
	 * Register a model loading plugin.
	 */
	static <T> void register(DataPreparator<T> preparator, PreparableModelLoadingPlugin<T> plugin) {
		ModelLoadingEventDispatcher.registerPlugin(preparator, plugin);
	}

	/**
	 * Called towards the beginning of the model loading process, every time resource are (re)loaded.
	 * Use the context object to extend model loading as desired.
	 */
	void onInitializeModelLoader(T preparedData, ModelLoadingPlugin.Context pluginContext);

	@FunctionalInterface
	interface DataPreparator<T> {
		/**
		 * Load data from the passed resource manager, using the passed executor.
		 * Do not block the thread, rather use {@link CompletableFuture#supplyAsync(Supplier, Executor)} to compute the data.
		 */
		CompletableFuture<T> load(ResourceManager resourceManager, Executor executor);
	}
}
