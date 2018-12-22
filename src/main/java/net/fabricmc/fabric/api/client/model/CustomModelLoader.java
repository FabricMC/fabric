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

package net.fabricmc.fabric.api.client.model;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * Interface for custom model loaders.
 *
 * Custom model loaders hook the part where a ModelIdentifier is resolved in
 * the loaded resource manager and loaded as an UnbakedModel.
 *
 * As CustomModelLoaders are instantiated with a new ModelLoader, it is safe
 * (and recommended!) to cache information inside a loader.
 *
 * Keep in mind that only *one* CustomModelLoader may respond to a given model
 * at any time. If you're writing, say, an OBJ loader, this means you could
 * easily conflict with another OBJ loader unless you take some precautions,
 * for example:
 *
 * a) Only load files with a mod-suffixed name, such as .architect.obj,
 * b) Only load files from an explicit list of namespaces, registered elsewhere.
 */
public interface CustomModelLoader {
	/**
	 * @param id The model identifier. Vanilla provides a {@link Identifier} or {@link ModelIdentifier}.
	 * @return True if this loader can load a given identifier.
	 */
	boolean accepts(Identifier id);

	/**
	 * @param id The model identifier. Vanilla provides a {@link Identifier} or {@link ModelIdentifier}.
	 * @return The loaded UnbakedModel.
	 */
	UnbakedModel load(Identifier id) throws CustomModelLoaderException;
}
