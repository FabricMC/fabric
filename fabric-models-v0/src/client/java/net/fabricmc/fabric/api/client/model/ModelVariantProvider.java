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
import net.minecraft.client.util.ModelIdentifier;

/**
 * Interface for model variant providers.
 *
 * <p>Model variant providers hook the resolution of ModelIdentifiers. In vanilla, this is
 * the part where a "minecraft:stone#normal" identifier triggers the loading of a
 * "minecraft:models/stone" model ({@link ModelResourceProvider} handles the later step).
 *
 * <p>The most common use of this is to cooperate with a {@link ModelAppender}, but it can
 * also allow you to add your own block- or item-state formats. To trigger the loading
 * of another model, use the passed {@link ModelProviderContext}.
 *
 * <p>As every model loading is instantiated with a new provider, it is safe
 * (and recommended!) to cache information.
 *
 * <p>Keep in mind that only *one* ModelVariantProvider may respond to a given model
 * at any time.
 */
@FunctionalInterface
public interface ModelVariantProvider {
	/**
	 * @param modelId The model identifier, complete with variant.
	 * @return The loaded UnbakedModel, or null if this ModelVariantProvider doesn't handle a specific Identifier
	 * (or if there was no error!).
	 */
	@Nullable
	UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException;
}
