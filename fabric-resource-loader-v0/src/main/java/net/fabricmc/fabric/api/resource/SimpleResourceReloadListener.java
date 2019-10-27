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

package net.fabricmc.fabric.api.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

/**
 * A simplified version of the "resource reload listener" interface, hiding the
 * peculiarities of the API.
 *
 * <p>In essence, there are two stages:
 *
 * <ul><li>load: create an instance of your data object containing all loaded and
 * processed information,
 * <li>apply: apply the information from the data object to the game instance.</ul>
 *
 * <p>The load stage should be self-contained as it can run on any thread! However,
 * the apply stage is guaranteed to run on the game thread.
 *
 * <p>For a fully synchronous alternative, consider using
 * {@link SynchronousResourceReloadListener} in conjunction with
 * {@link IdentifiableResourceReloadListener}.
 *
 * @param <T> The data object.
 */
public interface SimpleResourceReloadListener<T> extends IdentifiableResourceReloadListener {
	@Override
	default CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer helper, ResourceManager manager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor) {
		return load(manager, loadProfiler, loadExecutor).thenCompose(helper::whenPrepared).thenCompose(
			(o) -> apply(o, manager, applyProfiler, applyExecutor)
		);
	}

	/**
	 * Asynchronously process and load resource-based data. The code
	 * must be thread-safe and not modify game state!
	 *
	 * @param manager  The resource manager used during reloading.
	 * @param profiler The profiler which may be used for this stage.
	 * @param executor The executor which should be used for this stage.
	 * @return A CompletableFuture representing the "data loading" stage.
	 */
	CompletableFuture<T> load(ResourceManager manager, Profiler profiler, Executor executor);

	/**
	 * Synchronously apply loaded data to the game state.
	 *
	 * @param manager  The resource manager used during reloading.
	 * @param profiler The profiler which may be used for this stage.
	 * @param executor The executor which should be used for this stage.
	 * @return A CompletableFuture representing the "data applying" stage.
	 */
	CompletableFuture<Void> apply(T data, ResourceManager manager, Profiler profiler, Executor executor);
}
