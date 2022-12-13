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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.joml.Vector3d;

/**
 * Instantiate this class in order to provide an optional set of <code>display</code> properties for a given model JSON.
 */
public class DisplayBuilder {
	private final Position position;
	private Vector3d rotation = new Vector3d();
	private Vector3d translation = new Vector3d();
	private Vector3d scale = new Vector3d();

	/**
	 * Create a new display builder with a given display position to apply a transformation for.
	 *
	 * @param position The corresponding {@link Position} to apply this display to.
	 */
	public DisplayBuilder(Position position) {
		this.position = position;
	}

	public String getPositionKey() {
		return position.name().toLowerCase();
	}

	/**
	 * Rotate the model by the given vector.
	 *
	 * @param x The X-coordinate of the rotation.
	 * @param y The Y-coordinate of the rotation.
	 * @param z The Z-coordinate of the rotation.
	 * @return The current newly-modified {@link DisplayBuilder} instance.
	 */
	public DisplayBuilder rotate(double x, double y, double z) {
		return rotate(new Vector3d(x, y, z));
	}

	/**
	 * Rotate the model by the given vector.
	 *
	 * @param vec A rotation vector.
	 * @return The current newly-modified {@link DisplayBuilder} instance.
	 */
	public DisplayBuilder rotate(Vector3d vec) {
		this.rotation = vec;
		return this;
	}

	/**
	 * Translate the model by the given vector.
	 *
	 * @param x The X-coordinate of the translation.
	 * @param y The Y-coordinate of the translation.
	 * @param z The Z-coordinate of the translation.
	 * @return The current newly-modified {@link DisplayBuilder} instance.
	 */
	public DisplayBuilder translate(double x, double y, double z) {
		return translate(new Vector3d(x, y, z));
	}

	/**
	 * Translate the model by the given vector.
	 *
	 * @param vec A translation vector.
	 * @return The current newly-modified {@link DisplayBuilder} instance.
	 */
	public DisplayBuilder translate(Vector3d vec) {
		this.translation = vec;
		return this;
	}

	/**
	 * Scale the model by the given vector.
	 *
	 * @param x The scalar for the X-direction.
	 * @param y The scalar for the Y-direction.
	 * @param z The scalar for the Z-direction.
	 * @return The current newly-modified {@link DisplayBuilder} instance.
	 */
	public DisplayBuilder scale(double x, double y, double z) {
		return scale(new Vector3d(x, y, z));
	}

	/**
	 * Scale the model by the given vector.
	 *
	 * @param vec A scale vector.
	 * @return The current newly-modified {@link DisplayBuilder} instance.
	 */
	public DisplayBuilder scale(Vector3d vec) {
		this.scale = vec;
		return this;
	}

	private JsonArray vecArray(Vector3d vec) {
		JsonArray entry = new JsonArray();
		entry.add(vec.x());
		entry.add(vec.y());
		entry.add(vec.z());
		return entry;
	}

	public JsonObject build() {
		JsonObject display = new JsonObject();

		if (rotation != rotation.zero()) {
			display.add("rotation", vecArray(rotation));
		}

		if (translation != translation.zero()) {
			display.add("translation", vecArray(translation));
		}

		if (scale != scale.zero()) {
			display.add("scale", vecArray(scale));
		}

		return display;
	}

	/**
	 * The place where a given set of display transformations should be applied. <code>FIXED</code> refers to the item
	 * model as attached to an <em>item frame</em>, while the others are as given.
	 */
	public enum Position {
		THIRDPERSON_RIGHTHAND,
		THIRDPERSON_LEFTHAND,
		FIRSTPERSON_RIGHTHAND,
		FIRSTPERSON_LEFTHAND,
		GUI,
		HEAD,
		GROUND,
		FIXED
	}
}
