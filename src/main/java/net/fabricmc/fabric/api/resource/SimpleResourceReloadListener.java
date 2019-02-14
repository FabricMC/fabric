/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A simplified version of the "resource reload listener" interface, hiding the
 * peculiarities of the API.
 *
 * In essence, there are two stages:
 *
 * - load: create an instance of your data object containing all loaded and
 *   processed information,
 * - apply: apply the information from the data object to the game instance.
 *
 * The load stage should be self-contained as it can run on any thread! However,
 * the apply stage is guaranteed to run on the game thread.
 *
 * For a fully synchronous alternative, consider using
 * {@link SynchronousResourceReloadListener} in conjunction with
 * {@link IdentifiableResourceReloadListener}.
 *
 * @param <T> The data object.
 */
public interface SimpleResourceReloadListener<T> extends IdentifiableResourceReloadListener {
	default CompletableFuture<Void> apply(ResourceReloadListener.Helper var1, ResourceManager var2, Profiler var3, Profiler var4, Executor var5, Executor var6) {
		return load(var2, var3, var5).thenCompose(var1::waitForAll).thenCompose(
			(o) -> apply(o, var2, var4, var6)
		);
	}

	CompletableFuture<T> load(ResourceManager manager, Profiler profiler, Executor executor);

	CompletableFuture<Void> apply(T data, ResourceManager manager, Profiler profiler, Executor executor);
}
