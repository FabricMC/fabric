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

import java.util.List;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;

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
	 * Generates an item model based on a model template, texture mapping and specific set of given model elements.
	 *
	 * @param item The item to generate this model for.
	 * @param model The desired template to generate this model out of.
	 * @param textureMap The desired texture mapping for this model.
	 * @param elements A list of {@link ElementBuilder}s from which to generate individual model elements.
	 */
	default void register(Item item, Model model, TextureMap textureMap, ElementBuilder... elements) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Generates an item model based on a model template, texture mapping and specific set of given overrides.
	 *
	 * @param item The item to generate this model for.
	 * @param model The desired template to generate this model out of.
	 * @param textureMap The desired texture mapping for this model.
	 * @param overrides A list of {@link OverrideBuilder}s from which to generate individual model overrides.
	 */
	default void register(Item item, Model model, TextureMap textureMap, OverrideBuilder... overrides) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Generates an item model based on a model template and texture mapping, with an optional <code>gui_light</code>
	 * property.
	 *
	 * @param item The item to generate this model for.
	 * @param model The desired template to generate this model out of.
	 * @param textureMap The desired texture mapping for this model.
	 * @param guiLight A {@link FabricModel.GuiLight} entry, either <code>FRONT</code> or <code>SIDE</code>.
	 */
	default void register(Item item, Model model, TextureMap textureMap, FabricModel.GuiLight guiLight) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Generates an item model based on a model template, texture mapping and specific set of given model elements and
	 * overrides.
	 *
	 * @param item The item to generate this model for.
	 * @param model The desired template to generate this model out of.
	 * @param textureMap The desired texture mapping for this model.
	 * @param elements A list of {@link ElementBuilder}s from which to generate individual model elements.
	 * @param overrides A list of {@link OverrideBuilder}s from which to generate individual model overrides.
	 */
	default void register(Item item, Model model, TextureMap textureMap, List<ElementBuilder> elements, List<OverrideBuilder> overrides) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Generates an item model based on a model template, texture mapping and specific set of given model elements and
	 * overrides.
	 *
	 * @param item The item to generate this model for.
	 * @param model The desired template to generate this model out of.
	 * @param textureMap The desired texture mapping for this model.
	 * @param elements A list of {@link ElementBuilder}s from which to generate individual model elements.
	 * @param overrides A list of {@link OverrideBuilder}s from which to generate individual model overrides.
	 */
	default void register(Item item, Model model, TextureMap textureMap, FabricModel.GuiLight guiLight, List<ElementBuilder> elements, List<OverrideBuilder> overrides) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
