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
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * Model resolvers are able to provide a custom model for specific {@link Identifier}s.
 * In vanilla, these {@link Identifier}s are converted to file paths and used to load
 * a model from JSON. Since model resolvers override this process, they can be used to
 * create custom model formats.
 *
 * <p>Only one resolver may provide a custom model for a certain {@link Identifier}.
 * Thus, resolvers that load models using a custom format could conflict. To avoid
 * conflicts, such resolvers may want to only load files with a mod-suffixed name
 * or only load files that have been explicitly defined elsewhere.
 *
 * <p>If it is necessary to load and bake an arbitrary model that is not referenced
 * normally, a model resolver can be used in conjunction with
 * {@link ModelLoadingPlugin.Context#addModels} to directly load and bake custom model
 * instances.
 *
 * <p>Model resolvers are invoked for <b>every single model that will be loaded</b>,
 * so implementations should be as efficient as possible.
 *
 * @see ModelLoadingPlugin.Context#addModels
 */
@FunctionalInterface
public interface ModelResolver {
	/**
	 * @return the resolved {@link UnbakedModel}, or {@code null} if this resolver does not handle the current {@link Identifier}
	 */
	@Nullable
	UnbakedModel resolveModel(Context context);

	/**
	 * The context for model resolution.
	 */
	@ApiStatus.NonExtendable
	interface Context {
		/**
		 * The identifier of the model to be loaded.
		 */
		Identifier id();

		/**
		 * Loads a model using an {@link Identifier} or {@link ModelIdentifier}, or gets it if it was already loaded.
		 *
		 * @param id the model identifier
		 * @return the unbaked model, or a missing model if it is not present
		 */
		UnbakedModel getOrLoadModel(Identifier id);

		/**
		 * The current model loader instance, which changes between resource reloads.
		 *
		 * <p>Do <b>not</b> call {@link ModelLoader#getOrLoadModel} as it does not supported nested model resolution;
		 * use {@link #getOrLoadModel} from the context instead.
		 */
		ModelLoader loader();
	}
}
