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

package net.fabricmc.fabric.api.datagen.v1.model;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;

import net.fabricmc.fabric.api.datagen.v1.model.builder.ModelBuilder;

/**
 * Fabric-provided extensions for {@link net.minecraft.data.client.ItemModelGenerator}.
 *
 * <p>Note: This interface is automatically implemented on all generators via Mixin and interface injection.
 */
public interface FabricItemModelGenerator {
	/**
	 * Generates an item's model based on the given model template and corresponding texture mapping. Both the model and
	 * mapping must be made with the same set of texture keys.
	 *
	 * @param item The item to generate this model for.
	 * @param model The desired template to generate this model out of.
	 * @param textureMap The desired texture mapping for this model.
	 */
	default void register(Item item, Model model, TextureMap textureMap) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Generates an item's model based on an already-prepared {@link ModelBuilder}. Textures get automatically mapped to
	 * the resulting model when built.
	 *
	 * @param item The item to generate this model for.
	 * @param modelBuilder The desired model builder to generate from.
	 */
	default void build(Item item, ModelBuilder<?> modelBuilder) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Generates an item's model based on the given model template and corresponding texture mapping. Both the model and
	 * mapping must be made with the same set of texture keys.
	 *
	 * @param item The item to generate this model for.
	 * @param suffix An optional suffix for the generated model's file name.
	 * @param model The desired template to generate this model out of.
	 * @param textureMap The desired texture mapping for this model.
	 */
	default void register(Item item, String suffix, Model model, TextureMap textureMap) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Generates an item's model based on an already-prepared {@link ModelBuilder}. Textures get automatically mapped to
	 * the resulting model when built.
	 *
	 * @param item The item to generate this model for.
	 * @param suffix An optional suffix for the generated model's file name.
	 * @param modelBuilder The desired model builder to generate from.
	 */
	default void build(Item item, String suffix, ModelBuilder<?> modelBuilder) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
