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

import java.util.EnumMap;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import net.minecraft.util.math.Direction;

/**
 * Instantiate this class in order to provide any specific <code>elements</code> to a given block/item model JSON.
 */
public final class ElementBuilder {
	private final Vector3d from;
	private final Vector3d to;
	@Nullable
	private RotationBuilder rotation = null;
	private boolean shade = true;
	private final EnumMap<Direction, FaceBuilder> faces = new EnumMap<>(Direction.class);

	/**
	 * Create a new element builder with a given pair of opposite vertices and optional {@link RotationBuilder} and
	 * shading.
	 *
	 * @param from The vertex to start drawing out a cuboid element from, given as a {@link Vector3d}.
	 * @param to The vertex to stop drawing the element at, given as a {@link Vector3d}.
	 */
	public ElementBuilder(Vector3d from, Vector3d to) {
		double[] components = {from.x, from.y, from.z, to.x, to.y, to.z};

		for (double c : components) {
			Preconditions.checkArgument(c >= -16 && c <= 32, "Component out of range");
		}

		this.from = from;
		this.to = to;
	}

	/**
	 * Create a new element builder with a given pair of opposite vertices.
	 *
	 * @param fromX The X-coordinate of the vertex to start drawing out a cuboid element from.
	 * @param fromY The Y-coordinate of the vertex to start drawing out a cuboid element from.
	 * @param fromZ The Z-coordinate of the vertex to start drawing out a cuboid element from.
	 * @param toX The X-coordinate of the vertex to stop drawing the element at.
	 * @param toY The Y-coordinate of the vertex to stop drawing the element at.
	 * @param toZ The Z-coordinate of the vertex to stop drawing the element at.
	 */
	public ElementBuilder(double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
		this(new Vector3d(fromX, fromY, fromZ), new Vector3d(toX, toY, toZ));
	}

	/**
	 * Sets a rotation to be applied to the element being built.
	 *
	 * @param rotation An instance of a {@link RotationBuilder} to provide a <code>rotation</code> for the element, or
	 *                 <code>null</code> for no rotation.
	 */
	public ElementBuilder withRotation(@Nullable RotationBuilder rotation) {
		this.rotation = rotation;
		return this;
	}

	/**
	 * Toggles whether to render shadows cast by the element being built.
	 */
	public ElementBuilder withShading(boolean shade) {
		this.shade = shade;
		return this;
	}

	/**
	 * Provides an element builder with a given {@link FaceBuilder} to provide data for a specific face, such as
	 * texturing.
	 *
	 * @param face The face to specify this data for.
	 * @param builder An instanced {@link FaceBuilder} from which to build this face's data.
	 */
	public ElementBuilder addFace(Direction face, FaceBuilder builder) {
		this.faces.put(face, builder);
		return this;
	}

	/**
	 * Removes the specified face for this builder.
	 *
	 * @param face The face direction whose {@link FaceBuilder} to remove.
	 */
	public ElementBuilder removeFace(Direction face) {
		this.faces.remove(face);
		return this;
	}

	/**
	 * Clears all current faces for this builder.
	 */
	public ElementBuilder clearFaces() {
		this.faces.clear();
		return this;
	}

	@ApiStatus.Internal
	public JsonObject build() {
		JsonObject element = new JsonObject();

		JsonArray from = new JsonArray();
		from.add(this.from.x());
		from.add(this.from.y());
		from.add(this.from.z());
		element.add("from", from);

		JsonArray to = new JsonArray();
		to.add(this.to.x());
		to.add(this.to.y());
		to.add(this.to.z());
		element.add("to", to);

		if (rotation != null) {
			element.add("rotation", rotation.build());
		}

		if (!shade) {
			element.addProperty("shade", false);
		}

		JsonArray faces = new JsonArray();
		this.faces.forEach((f, b) -> {
			Preconditions.checkArgument(f != null);
			JsonObject face = new JsonObject();
			face.add(f.name().toLowerCase(), b.build());
			faces.add(face);
		});
		element.add("faces", faces);

		return element;
	}
}
