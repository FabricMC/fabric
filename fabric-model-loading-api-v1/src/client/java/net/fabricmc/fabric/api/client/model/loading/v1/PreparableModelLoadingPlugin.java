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

import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;

/**
 * A model loading plugin is used to extend the model loading process through the passed
 * {@link ModelLoadingPlugin.Context} object.
 *
 * <p>This version of {@link ModelLoadingPlugin} allows loading ("preparing") some data off-thread in parallel before
 * the model loading process starts. Usually, this means loading some resources from the provided
 * {@link ResourceManager}.
 */
@FunctionalInterface
public interface PreparableModelLoadingPlugin<T> {
	/**
	 * Registers a preparable model loading plugin.
	 */
	static <T> void register(DataLoader<T> loader, PreparableModelLoadingPlugin<T> plugin) {
		ModelLoadingPluginManager.registerPlugin(loader, plugin);
	}

	/**
	 * Called towards the beginning of the model loading process, every time resource are (re)loaded.
	 * Use the context object to extend model loading as desired.
	 *
	 * @param data The data loaded by the {@link DataLoader}.
	 * @param pluginContext The context that can be used to extend model loading.
	 */
	void onInitializeModelLoader(T data, ModelLoadingPlugin.Context pluginContext);

	@FunctionalInterface
	interface DataLoader<T> {
		/**
		 * Returns a {@link CompletableFuture} that will load the data.
		 * Do not block the thread when this function is called, rather use
		 * {@link CompletableFuture#supplyAsync(Supplier, Executor)} to compute the data.
		 * The completable future should be scheduled to run using the passed executor.
		 *
		 * @param resourceManager The resource manager that can be used to retrieve resources.
		 * @param executor The executor that <b>must</b> be used to schedule any completable future.
		 */
		CompletableFuture<T> load(ResourceManager resourceManager, Executor executor);
	}
}
