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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.property.DisplayBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.property.ElementBuilder;

/**
 * Base builder class used for general model files. Can be extended if necessary to allow for any custom models not typically associated with blocks and items.
 *
 * <p>Note that custom non-block/item models may generally need to be generated under {@link net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider#generateItemModels(ItemModelGenerator)}.
 */
@SuppressWarnings("unchecked")
public abstract class ModelBuilder<T extends ModelBuilder<T>> {
	private final Identifier parent;
	private final Set<TextureKey> requiredTextures = new HashSet<>();
	private final HashMap<TextureKey, Identifier> textures = new HashMap<>();
	private final EnumMap<DisplayBuilder.Position, DisplayBuilder> displays = new EnumMap<>(DisplayBuilder.Position.class);
	private final List<ElementBuilder> elements = new ArrayList<>();

	@ApiStatus.Internal
	protected ModelBuilder(Identifier parent) {
		this.parent = parent;
	}

	/**
	 * Adds a texture key for this model and associates a texture file with it. If the key already exists for this
	 * builder, its texture will simply be replaced. If using a {@link TextureKey} instance, an optional parent for the
	 * key itself can be specified.
	 *
	 * @param key An instanced {@link TextureKey} to hold the texture for.
	 * @param texture The namespaced ID of the texture for this key.
	 */
	public T addTexture(TextureKey key, Identifier texture) {
		this.requiredTextures.add(key);
		this.textures.put(key, texture);
		return (T) this;
	}

	/**
	 * Adds a texture key for this model and associates a texture file with it. If the key already exists for this
	 * builder, its texture will simply be replaced.
	 *
	 * @param key A key to hold the texture for.
	 * @param texture The namespaced ID of the texture for this key.
	 */
	public T addTexture(String key, Identifier texture) {
		return addTexture(TextureKey.of(key), texture);
	}

	/**
	 * Removes a texture and its key from this model builder.
	 *
	 * @param key The texture key to remove.
	 */
	public T removeTexture(TextureKey key) {
		this.requiredTextures.remove(key);
		this.textures.remove(key);
		return (T) this;
	}

	/**
	 * Clears all current textures and texture keys for this model builder.
	 */
	public T clearTextures() {
		this.requiredTextures.clear();
		this.textures.clear();
		return (T) this;
	}

	/**
	 * Adds an entry to the <code>display</code> property of the model. Display entries consist of a linear
	 * transformation (translation, rotation and scaling) applied to a given "position" that the item (or BlockItem)
	 * with the given model may be in, e.g. whether it's being displayed in a
	 * player's hand or in a GUI.
	 *
	 * @param position A {@link DisplayBuilder.Position} to set the property for. Can be either hand of either first or
	 *                 third person, the head, the ground (as a dropped item), within GUIs or within item frames
	 *                 (<code>FIXED</code>).
	 * @param display A {@link DisplayBuilder} to build the required display property from.
	 */
	public T addDisplay(DisplayBuilder.Position position, DisplayBuilder display) {
		this.displays.put(position, display);
		return (T) this;
	}

	/**
	 * Removes a display entry from this model builder.
	 *
	 * @param position The display position whose entry to remove.
	 */
	public T removeDisplay(DisplayBuilder.Position position) {
		this.displays.remove(position);
		return (T) this;
	}

	/**
	 * Clears all current {@link DisplayBuilder}s for this model builder.
	 */
	public T clearDisplays() {
		this.displays.clear();
		return (T) this;
	}

	/**
	 * Adds an entry to the <code>elements</code> property of the model. Element entries consist of a pair of opposite
	 * vertices of a cuboid to draw the element out as, with an optional rotation, shading and set of rendered faces.
	 *
	 * @param element An {@link ElementBuilder} to build an individual entry from.
	 */
	public T addElement(ElementBuilder element) {
		this.elements.add(element);
		return (T) this;
	}

	/**
	 * Clears all current {@link ElementBuilder}s for this model builder.
	 */
	public T clearElements() {
		this.elements.clear();
		return (T) this;
	}

	/**
	 * @return A completed {@link Model} to generate alongside some texture map, which may or may not also be provided
	 * via this builder.
	 */
	public Model buildModel() {
		TextureKey[] textures = Arrays.copyOf(requiredTextures.toArray(), requiredTextures.size(), TextureKey[].class);
		Model model = new Model(Optional.ofNullable(parent), Optional.empty(), textures);

		displays.forEach(model::fabric_withDisplay);
		elements.forEach(model::fabric_addElement);
		return model;
	}

	/**
	 * @return A completed {@link TextureMap} to accompany any models built by this builder or otherwise.
	 */
	public TextureMap mapTextures() {
		TextureMap map = new TextureMap();
		textures.forEach(map::put);
		return map;
	}
}
