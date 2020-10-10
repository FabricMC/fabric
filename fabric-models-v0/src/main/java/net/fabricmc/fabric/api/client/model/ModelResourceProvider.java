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

package net.fabricmc.fabric.api.client.model;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

/**
 * Interface for model resource providers.
 *
 * <p>Model resource providers hook the loading of model *files* from the resource tree;
 * that is, in vanilla, it handles going from "minecraft:block/stone" to a
 * "assets/minecraft/models/block/stone.json" file.
 *
 * <p>This is where you want to add your own custom model formats.
 *
 * <p>As providers are instantiated with a new provider, it is safe
 * (and recommended!) to cache information inside a loader.
 *
 * <p>Keep in mind that only *one* ModelResourceProvider may respond to a given model
 * at any time. If you're writing, say, an OBJ loader, this means you could
 * easily conflict with another OBJ loader unless you take some precautions,
 * for example:
 *
 * <ul><li>Only load files with a mod-suffixed name, such as .architect.obj,
 * <li>Only load files from an explicit list of namespaces, registered elsewhere.</ul>
 */
@FunctionalInterface
public interface ModelResourceProvider {
	/**
	 * @param resourceId The resource identifier to be loaded.
	 * @return The loaded UnbakedModel, or null if this ModelResourceProvider doesn't handle a specific Identifier
	 * (or if there was no error!).
	 */
	@Nullable
	UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException;
}
