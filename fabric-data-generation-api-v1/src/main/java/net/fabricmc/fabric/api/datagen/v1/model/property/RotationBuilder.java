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
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

import net.minecraft.util.math.Direction;

/**
 * Instantiate this class in order to provide an optional <code>rotation</code> to an element of a JSON model.
 */
public final class RotationBuilder {
	private final Vector3d origin;
	private final Direction.Axis axis;
	private final Angle angle;
	private boolean rescale = false;

	/**
	 * Create a new rotation builder with a given origin, axis, angle and optional rescaling.
	 *
	 * @param origin An origin point to rotate around, passed as a {@link Vector3d}.
	 * @param axis The coordinate axis to rotate around (either X, Y or Z).
	 * @param angle The angle of rotation. Limited to 45° through -45° in increments of 22.5°.
	 */
	public RotationBuilder(Vector3d origin, Direction.Axis axis, Angle angle) {
		this.origin = origin;
		this.axis = axis;
		this.angle = angle;
	}

	/**
	 * Create a new rotation builder with a given origin, axis, angle and optional rescaling.
	 *
	 * @param x The X-coordinate of the origin point to rotate around.
	 * @param y The Y-coordinate of the origin point to rotate around.
	 * @param z The Z-coordinate of the origin point to rotate around.
	 * @param axis The coordinate axis to rotate around (either X, Y or Z).
	 * @param angle The angle of rotation. Limited to 45° through -45° in increments of 22.5°.
	 */
	public RotationBuilder(double x, double y, double z, Direction.Axis axis, Angle angle) {
		this(new Vector3d(x, y, z), axis, angle);
	}

	/**
	 * Toggles whether to scale the rotated faces across the whole block. Defaults to <code>false</code>.
	 */
	public RotationBuilder rescale(boolean rescale) {
		this.rescale = rescale;
		return this;
	}

	@ApiStatus.Internal
	public JsonObject build() {
		JsonObject rotation = new JsonObject();

		JsonArray origin = new JsonArray();
		origin.add(this.origin.x());
		origin.add(this.origin.y());
		origin.add(this.origin.z());
		rotation.add("origin", origin);

		rotation.addProperty("axis", axis.getName());
		rotation.addProperty("angle", angle.getAngle());

		if (rescale) {
			rotation.addProperty("rescale", true);
		}

		return rotation;
	}

	public enum Angle {
		PLUS45(45),
		PLUS22_5(22.5f),
		ZERO(0),
		MINUS22_5(-22.5f),
		MINUS45(-45);

		private final float angle;

		Angle(float angle) {
			this.angle = angle;
		}

		public float getAngle() {
			return angle;
		}
	}
}
