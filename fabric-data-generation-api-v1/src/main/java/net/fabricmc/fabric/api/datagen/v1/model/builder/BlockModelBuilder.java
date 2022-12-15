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

import java.util.Map;

import com.google.common.base.Preconditions;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.property.DisplayBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.property.ElementBuilder;
import net.fabricmc.fabric.mixin.datagen.TextureMapAccessor;

public class BlockModelBuilder extends ModelBuilder {
	private boolean occlude = true;

	private BlockModelBuilder(Identifier parent) {
		super(parent);
	}

	public static BlockModelBuilder createNew(Identifier parent) {
		return new BlockModelBuilder(parent);
	}

	public static BlockModelBuilder copyFrom(Model model, TextureMap textures) {
		Map<TextureKey, Identifier> copyTextures = ((TextureMapAccessor) textures).getEntries();
		Preconditions.checkArgument(copyTextures.keySet().equals(model.getRequiredTextures()), "Texture map does not match slots for provided model " + model);

		BlockModelBuilder builder = new BlockModelBuilder(model.getParent().orElse(null));
		builder.requiredTextures.addAll(model.getRequiredTextures());
		builder.textures.putAll(copyTextures);

		builder.displays.putAll(model.getDisplayBuilders());
		builder.elements.addAll(model.getElementBuilders());
		builder.occlude = model.getAmbientOcclusion();

		return builder;
	}

	@Override
	public BlockModelBuilder addTexture(TextureKey key, Identifier texture) {
		return (BlockModelBuilder) super.addTexture(key, texture);
	}

	@Override
	public BlockModelBuilder addTexture(String key, Identifier texture) {
		return (BlockModelBuilder) super.addTexture(key, texture);
	}

	@Override
	public BlockModelBuilder addDisplay(DisplayBuilder.Position position, DisplayBuilder display) {
		return (BlockModelBuilder) super.addDisplay(position, display);
	}

	@Override
	public BlockModelBuilder addElement(ElementBuilder element) {
		return (BlockModelBuilder) super.addElement(element);
	}

	/**
	 * Disables ambient occlusion for this model.
	 */
	public BlockModelBuilder noAmbientOcclusion() {
		this.occlude = false;
		return this;
	}

	@Override
	public Model buildModel() {
		Model blockModel = super.buildModel();
		blockModel.setAmbientOcclusion(occlude);
		return blockModel;
	}
}
