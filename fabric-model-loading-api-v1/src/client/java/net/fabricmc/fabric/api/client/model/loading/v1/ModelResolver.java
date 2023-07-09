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
 * Contains interfaces for the events mods can use to load their own model formats.
 *
 * <p>These hooks fire for <b>every single model that's loaded</b>, so the code written here should be as simple/performant
 * as possible.
 */
public final class ModelResolver {
	/**
	 * Interface for model resource resolvers.
	 *
	 * <p>Model resource resolvers hook the loading of model *files* from the resource tree;
	 * that is, in vanilla, it handles going from "minecraft:block/stone" to a
	 * "assets/minecraft/models/block/stone.json" file.
	 *
	 * <p>A common use of this is to cooperate with {@link ModelLoadingPlugin.Context#addModels} to directly
	 * add custom {@link UnbakedModel} instances.
	 *
	 * <p>This is also where you want to add your own custom model formats.
	 *
	 * <p>As each model reload uses a new resolver, it is safe
	 * (and recommended!) to cache information inside a loader.
	 *
	 * <p>Keep in mind that only *one* Resource may respond to a given model
	 * at any time. If you're writing, say, an OBJ loader, this means you could
	 * easily conflict with another OBJ loader unless you take some precautions,
	 * for example:
	 *
	 * <ul><li>Only load files with a mod-suffixed name, such as .architect.obj,
	 * <li>Only load files from an explicit list of namespaces, registered elsewhere.</ul>
	 */
	@FunctionalInterface
	public interface Resource {
		/**
		 * @param resourceId The resource identifier to be loaded.
		 * @return The loaded UnbakedModel, or null if this Resource doesn't handle a specific Identifier
		 * (or if there was no error!).
		 */
		@Nullable
		UnbakedModel resolveModelResource(Identifier resourceId, Context context);
	}

	/**
	 * The context used during model resolution.
	 */
	@ApiStatus.NonExtendable
	public interface Context {
		/**
		 * Load a model using an {@link Identifier}, {@link ModelIdentifier}, ... or get it if it was already loaded.
		 *
		 * <p>Please note that the game engine keeps track of circular model loading calls on its own.
		 *
		 * @param id The model identifier.
		 * @return The UnbakedModel. Can return a missing model if it's not present!
		 */
		UnbakedModel getOrLoadModel(Identifier id);

		/**
		 * The current model loader instance (changes when resource packs reload).
		 *
		 * <p>Do <b>not</b> call {@link ModelLoader#getOrLoadModel} as it doesn't supported nested model resolution,
		 * use {@link #getOrLoadModel} from the context instead.
		 */
		ModelLoader loader();
	}

	private ModelResolver() { }
}
