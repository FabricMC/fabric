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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.joml.Vector3d;

/**
 * Instantiate this class in order to provide an optional <code>rotation</code> to an element of a JSON model.
 */
public class RotationBuilder {
	private final Vector3d origin;
	private final Axis axis;
	private final float angle;
	private final boolean rescale;

	/**
	 * Create a new rotation builder with a given origin, axis, angle and optional rescaling.
	 *
	 * @param origin An origin point to rotate around, passed as a {@link Vector3d}.
	 * @param axis The coordinate axis to rotate around (either X, Y or Z).
	 * @param angle The angle of rotation.
	 * @param rescale Whether to scale the rotated faces across the whole block. Internally defaults to
	 *                   <code>false</code>.
	 */
	public RotationBuilder(Vector3d origin, Axis axis, float angle, boolean rescale) {
		this.origin = origin;
		this.axis = axis;
		this.angle = angle;
		this.rescale = rescale;
	}

	/**
	 * Create a new rotation builder with a given origin, axis and angle.
	 *
	 * @param origin An origin point to rotate around, passed as a {@link Vector3d}.
	 * @param axis The coordinate axis to rotate around (either X, Y or Z).
	 * @param angle The angle of rotation.
	 */
	public RotationBuilder(Vector3d origin, Axis axis, float angle) {
		this(origin, axis, angle, false);
	}

	/**
	 * Create a new rotation builder with a given origin, axis, angle and optional rescaling.
	 *
	 * @param x The X-coordinate of the origin point to rotate around.
	 * @param y The Y-coordinate of the origin point to rotate around.
	 * @param z The Z-coordinate of the origin point to rotate around.
	 * @param axis The coordinate axis to rotate around (either X, Y or Z).
	 * @param angle The angle of rotation.
	 * @param rescale Whether to scale the rotated faces across the whole block. Internally defaults to
	 *                   <code>false</code>.
	 */
	public RotationBuilder(double x, double y, double z, Axis axis, float angle, boolean rescale) {
		this(new Vector3d(x, y, z), axis, angle, rescale);
	}

	/**
	 * Create a new rotation builder with a given origin, axis, angle and optional rescaling.
	 *
	 * @param x The X-coordinate of the origin point to rotate around.
	 * @param y The Y-coordinate of the origin point to rotate around.
	 * @param z The Z-coordinate of the origin point to rotate around.
	 * @param axis The coordinate axis to rotate around (either X, Y or Z).
	 * @param angle The angle of rotation.
	 */
	public RotationBuilder(double x, double y, double z, Axis axis, float angle) {
		this(new Vector3d(x, y, z), axis, angle, false);
	}

	/**
	 * The coordinate axis to apply a rotation around, as provided by a rotation builder.
	 */
	public enum Axis {
		X, Y, Z
	}

	public JsonObject build() {
		JsonObject rotation = new JsonObject();

		JsonArray origin = new JsonArray();
		origin.add(this.origin.x());
		origin.add(this.origin.y());
		origin.add(this.origin.z());
		rotation.add("origin", origin);

		rotation.addProperty("axis", axis.name().toLowerCase());
		rotation.addProperty("angle", angle);

		if (rescale) {
			rotation.addProperty("rescale", true);
		}

		return rotation;
	}
}
