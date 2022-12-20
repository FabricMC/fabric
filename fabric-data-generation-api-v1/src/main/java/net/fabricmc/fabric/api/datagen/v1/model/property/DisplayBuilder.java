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

package net.fabricmc.fabric.api.datagen.v1.model.property;

import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

/**
 * Instantiate this class in order to provide an optional set of <code>display</code> properties for a given model JSON.
 */
public final class DisplayBuilder {
	private Vector3d rotation = new Vector3d();
	private Vector3d translation = new Vector3d();
	private Vector3d scale = new Vector3d();

	/**
	 * Rotate the model by the given vector.
	 *
	 * @param x The X-coordinate of the rotation.
	 * @param y The Y-coordinate of the rotation.
	 * @param z The Z-coordinate of the rotation.
	 */
	public DisplayBuilder rotate(double x, double y, double z) {
		return rotate(new Vector3d(x, y, z));
	}

	/**
	 * Rotate the model by the given vector.
	 *
	 * @param vec A rotation vector.
	 */
	public DisplayBuilder rotate(Vector3d vec) {
		this.rotation = vec;
		return this;
	}

	/**
	 * Translate the model by the given vector. Note that the game internally clamps this vector between (-80, -80, -80)
	 * and (80, 80, 80).
	 *
	 * @param x The X-coordinate of the translation.
	 * @param y The Y-coordinate of the translation.
	 * @param z The Z-coordinate of the translation.
	 */
	public DisplayBuilder translate(double x, double y, double z) {
		return translate(new Vector3d(x, y, z));
	}

	/**
	 * Translate the model by the given vector. Note that the game internally clamps this vector between (-80, -80, -80)
	 * and (80, 80, 80).
	 * @param vec A translation vector.
	 */
	public DisplayBuilder translate(Vector3d vec) {
		this.translation = vec;
		return this;
	}

	/**
	 * Scale the model by the given vector. Note that the game internally clamps this vector between (-4, -4, -4) and
	 * (4, 4, 4).
	 *
	 * @param x The scalar for the X-direction.
	 * @param y The scalar for the Y-direction.
	 * @param z The scalar for the Z-direction.
	 */
	public DisplayBuilder scale(double x, double y, double z) {
		return scale(new Vector3d(x, y, z));
	}

	/**
	 * Scale the model by the given scale factor. Note that the game internally clamps this between -4 and 4.
	 *
	 * @param f The constant factor to scale the model by in all three cardinal directions.
	 */
	public DisplayBuilder scale(double f) {
		return scale(new Vector3d(f));
	}

	/**
	 * Scale the model by the given vector. Note that the game internally clamps this vector between (-4, -4, -4) and
	 * (4, 4, 4).
	 *
	 * @param vec A scale vector.
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

	@ApiStatus.Internal
	public JsonObject build() {
		JsonObject display = new JsonObject();
		Vector3d zero = new Vector3d();

		if (!Objects.equals(rotation, zero)) {
			display.add("rotation", vecArray(rotation));
		}

		if (!Objects.equals(translation, zero)) {
			display.add("translation", vecArray(translation));
		}

		if (!Objects.equals(scale, zero)) {
			display.add("scale", vecArray(scale));
		}

		return display;
	}

	/**
	 * The place where a given set of display transformations should be applied. <code>FIXED</code> refers to the item
	 * model as attached to an <em>item frame</em>, while the others are as their name implies.
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
