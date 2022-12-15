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

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.property.DisplayBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.property.ElementBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.property.OverrideBuilder;

public class ItemModelBuilder extends ModelBuilder {
	private final List<OverrideBuilder> overrides = new ArrayList<>();
	private GuiLight guiLight = null;

	private ItemModelBuilder(Identifier parent) {
		super(parent);
	}

	public static ItemModelBuilder createNew(Identifier parent) {
		return new ItemModelBuilder(parent);
	}

	public static ItemModelBuilder copyFrom(Model model) {
		ItemModelBuilder builder = new ItemModelBuilder(model.getParent().orElse(null));

		builder.requiredTextures.addAll(model.getRequiredTextures());
		builder.displays.putAll(model.getDisplayBuilders());
		builder.elements.addAll(model.getElementBuilders());

		builder.guiLight = model.getGuiLight();
		builder.overrides.addAll(model.getOverrideBuilders());

		return builder;
	}

	@Override
	public ItemModelBuilder addTextureKey(TextureKey texture) {
		return (ItemModelBuilder) super.addTextureKey(texture);
	}

	@Override
	public ItemModelBuilder addDisplay(DisplayBuilder.Position position, DisplayBuilder display) {
		return (ItemModelBuilder) super.addDisplay(position, display);
	}

	@Override
	public ItemModelBuilder addElement(ElementBuilder element) {
		return (ItemModelBuilder) super.addElement(element);
	}

	/**
	 * Sets the <code>gui_light</code> property for this model.
	 *
	 * @param guiLight Either one of the two {@link ItemModelBuilder.GuiLight} entries
	 *                 (<code>FRONT</code>/<code>SIDE</code>).
	 */
	public ItemModelBuilder setGuiLight(GuiLight guiLight) {
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

	@Override
	public Model build() {
		Model itemModel = super.build();
		itemModel.setGuiLight(guiLight);
		overrides.forEach(itemModel::addOverride);
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
