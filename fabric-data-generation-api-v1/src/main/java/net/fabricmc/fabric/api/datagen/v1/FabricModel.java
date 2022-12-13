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

package net.fabricmc.fabric.api.datagen.v1;

import net.minecraft.data.client.Model;

import net.fabricmc.fabric.api.datagen.v1.model.DisplayBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.ElementBuilder;
import net.fabricmc.fabric.api.datagen.v1.model.OverrideBuilder;

/**
 * Fabric-provided extensions for {@link Model}.
 *
 * <p>Note: This interface is automatically implemented on all models via Mixin and interface injection.
 */
public interface FabricModel {
	/**
	 * Adds an entry to the <code>elements</code> property of a model.
	 * Element entries consist of a pair of opposite vertices of a cuboid to draw the element out as, with an optional
	 * rotation, shading and set of rendered faces.
	 *
	 * @param position A {@link DisplayBuilder.Position} to set the property for. Can be either hand of either first or
	 *                    third person, the head, the ground (as a dropped item), within GUIs or within item frames
	 *                    (<code>FIXED</code>).
	 * @param builder A {@link DisplayBuilder} to build the required display property from.
	 * @return The current newly-modified {@link Model} instance.
	 */
	default Model withDisplay(DisplayBuilder.Position position, DisplayBuilder builder) {
		return (Model) this;
	}

	/**
	 * Adds an entry to the <code>elements</code> property of a model.
	 * Element entries consist of a pair of opposite vertices of a cuboid to draw the element out as, with an optional
	 * rotation, shading and set of rendered faces.
	 *
	 * @param builder An {@link ElementBuilder} to build an individual entry from
	 * @return The current newly-modified {@link Model} instance.
	 */
	default Model addElement(ElementBuilder builder) {
		return (Model) this;
	}

	/**
	 * Adds an entry to the <code>overrides</code> property of a model. Only useful for <em>item</em> models.
	 * Override entries consist of a model ID and a set of "predicates" to override upon, all represented as a float
	 * between 0 and 1.
	 *
	 * @param builder An {@link OverrideBuilder} to build an individual entry from
	 * @return The current newly-modified {@link Model} instance.
	 */
	default Model addOverride(OverrideBuilder builder) {
		return (Model) this;
	}

	/**
	 * Sets the <code>gui_light</code> property of a model JSON. Only useful for <em>item</em> models.
	 *
	 * @param light Either one of the two {@link GUILight} entries (<code>FRONT</code>/<code>SIDE</code>).
	 * @return The current newly-modified {@link Model} instance.
	 */
	default Model setGUILight(GUILight light) {
		return (Model) this;
	}

	/**
	 * Sets the <code>ambientocclusion</code> property of a model JSON. Only useful for <em>block</em> models and
	 * applicable to the parent model.
	 *
	 * @param occlude Whether to use ambient occlusion on the given block model. Defaults to <code>true</code> if not
	 *                directly specified.
	 * @return The current newly-modified {@link Model} instance.
	 */
	default Model setAmbientOcclusion(boolean occlude) {
		return (Model) this;
	}

	/**
	 * Possible entries for the <code>gui_light</code> property of an item model. Internally defaults to
	 * <code>SIDE</code> if not directly specified. "Flat" item models (i.e. those with the <code>item/generated</code>
	 * parent) usually inherit <code>FRONT</code> for this property.
	 */
	enum GUILight {
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
