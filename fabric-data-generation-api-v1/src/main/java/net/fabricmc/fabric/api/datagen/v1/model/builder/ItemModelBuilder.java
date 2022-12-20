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

package net.fabricmc.fabric.api.datagen.v1.model.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.property.OverrideBuilder;
import net.fabricmc.fabric.mixin.datagen.TextureMapAccessor;

/**
 * Dedicated builder class for standard Minecraft item models.
 */
public class ItemModelBuilder extends ModelBuilder<ItemModelBuilder> {
	private final List<OverrideBuilder> overrides = new ArrayList<>();
	private GuiLight guiLight = null;

	private ItemModelBuilder(Identifier parent) {
		super(parent);
	}

	/**
	 * Creates a fresh item model builder with the given ID to use as a parent for any resulting models and no prior texture keys.
	 *
	 * @param parent The ID to use as the model's <code>parent</code>. This ID cannot be changed once a builder is instantiated.
	 */
	public static ItemModelBuilder createNew(Identifier parent) {
		return new ItemModelBuilder(parent);
	}

	/**
	 * Creates an item model builder based off an existing model and associated texture mapping. The model and mapping
	 * <em>must both</em> have the same set of {@link TextureKey}s in order to be built properly by any Fabric-provided
	 * generator methods using the model builder.
	 *
	 * @param model The {@link Model} to inherit any existing properties from. Only properties specific to item models
	 *              will be inherited for this builder.
	 * @param textures A {@link TextureMap} to go with the inherited model and pull existing textures from.
	 */
	public static ItemModelBuilder copyFrom(Model model, TextureMap textures) {
		Map<TextureKey, Identifier> copyTextures = ((TextureMapAccessor) textures).getEntries();
		Preconditions.checkArgument(copyTextures.keySet().equals(model.fabric_getRequiredTextures()), "Texture map does not match slots for provided model " + model);

		ItemModelBuilder builder = new ItemModelBuilder(model.fabric_getParent().orElse(null));
		copyTextures.forEach(builder::addTexture);

		model.fabric_getDisplayBuilders().forEach(builder::addDisplay);
		model.fabric_getElementBuilders().forEach(builder::addElement);
		builder.guiLight = model.fabric_getGuiLight();
		builder.overrides.addAll(model.fabric_getOverrideBuilders());

		return builder;
	}

	/**
	 * Sets the <code>gui_light</code> property for this model.
	 *
	 * @param guiLight Either one of the two {@link ItemModelBuilder.GuiLight} entries
	 *                 (<code>FRONT</code>/<code>SIDE</code>), or <code>null</code> to omit it from the end model file.
	 */
	public ItemModelBuilder setGuiLight(@Nullable GuiLight guiLight) {
		this.guiLight = guiLight;
		return this;
	}

	/**
	 * Adds an entry to the <code>overrides</code> property of the model. Override entries consist of a model ID and a
	 * set of "predicates" to override upon, all represented as a float between 0 and 1.
	 *
	 * @param override An {@link OverrideBuilder} to build an individual entry from.
	 */
	public ItemModelBuilder addOverride(OverrideBuilder override) {
		this.overrides.add(override);
		return this;
	}

	/**
	 * Clears all current {@link OverrideBuilder}s for this model builder.
	 */
	public ItemModelBuilder clearOverrides() {
		this.overrides.clear();
		return this;
	}

	@Override
	public Model buildModel() {
		Model itemModel = super.buildModel();
		itemModel.fabric_setGuiLight(guiLight);
		overrides.forEach(itemModel::fabric_addOverride);
		return itemModel;
	}

	/**
	 * Possible entries for the <code>gui_light</code> property. Internally, this defaults to <code>SIDE</code> if not
	 * directly specified. "Flat" item models (i.e. those with the <code>item/generated</code> parent) usually inherit
	 * <code>FRONT</code> for this property.
	 */
	public enum GuiLight {
		/**
		 * Render as a flat item icon within GUIs.
		 */
		FRONT,
		/**
		 * Render at an angle similarly to a block model within GUIs.
		 */
		SIDE
	}
}
