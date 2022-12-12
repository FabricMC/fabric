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

import net.fabricmc.fabric.api.datagen.v1.builder.DisplayBuilder;
import net.fabricmc.fabric.api.datagen.v1.builder.ElementBuilder;
import net.fabricmc.fabric.api.datagen.v1.builder.OverrideBuilder;

/**
 * Fabric-provided extensions for {@link Model}.
 *
 * <p>Note: This interface is automatically implemented on all blocks via Mixin and interface injection.
 */
public interface FabricModel {
	default Model withDisplay(DisplayBuilder.Position position, DisplayBuilder builder) {
		return (Model) this;
	}

	default Model addElement(ElementBuilder builder) {
		return (Model) this;
	}

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
