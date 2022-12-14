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

package net.fabricmc.fabric.impl.datagen;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.builder.ItemModelBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.property.DisplayBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.property.ElementBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.property.OverrideBuilder;

public interface FabricModel {
	default Model withDisplay(DisplayBuilder.Position position, DisplayBuilder builder) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default EnumMap<DisplayBuilder.Position, DisplayBuilder> getDisplayBuilders() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default Model addElement(ElementBuilder builder) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default List<ElementBuilder> getElementBuilders() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default Model addOverride(OverrideBuilder builder) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default List<OverrideBuilder> getOverrideBuilders() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default Model setGuiLight(ItemModelBuilder.GuiLight light) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default ItemModelBuilder.GuiLight getGuiLight() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default Model setAmbientOcclusion(boolean occlude) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default boolean getAmbientOcclusion() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default Optional<Identifier> getParent() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default Optional<String> getVariant() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default Set<TextureKey> getRequiredTextures() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
