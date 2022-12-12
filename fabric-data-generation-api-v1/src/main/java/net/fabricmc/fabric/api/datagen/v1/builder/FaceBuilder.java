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

package net.fabricmc.fabric.api.datagen.v1.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.joml.Vector4i;

import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.VariantSettings;

/**
 * Instantiate this class in order to provide a set of <code>faces</code> to be rendered for an element of a JSON model.
 */
public class FaceBuilder {
	private final Vector4i uv;
	private final TextureKey texture;
	private final Face cullFace;
	private final VariantSettings.Rotation rotation;
	private final int tintIndex;

	/**
	 * Create a new face builder with a given UV, texture key, cull face, rotation and tint index.
	 *
	 * @param uv The UV area of a texture to use for this face, given as a {@link Vector4i}.
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 * @param cullFace If specified, this face need not render if a block is directly adjacent to the given one.
	 * @param rotation The rotation of this texture (quarter-turns only).
	 * @param tintIndex The tint index for this face, if applicable.
	 */
	public FaceBuilder(Vector4i uv, TextureKey texture, Face cullFace, VariantSettings.Rotation rotation, int tintIndex) {
		this.uv = uv;
		this.texture = texture;
		this.cullFace = cullFace;
		this.rotation = rotation;
		this.tintIndex = tintIndex;
	}

	/**
	 * Create a new face builder with a given UV, texture key, cull face and rotation.
	 *
	 * @param uv The UV area of a texture to use for this face, given as a {@link Vector4i}.
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 * @param cullFace If specified, this face need not render if a block is directly adjacent to the given one.
	 * @param rotation The rotation of this texture (quarter-turns only).
	 */
	public FaceBuilder(Vector4i uv, TextureKey texture, Face cullFace, VariantSettings.Rotation rotation) {
		this(uv, texture, cullFace, rotation, -1);
	}

	/**
	 * Create a new face builder with a given UV, texture key and cull face.
	 *
	 * @param uv The UV area of a texture to use for this face, given as a {@link Vector4i}.
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 * @param cullFace If specified, this face need not render if a block is directly adjacent to the given one.
	 */
	public FaceBuilder(Vector4i uv, TextureKey texture, Face cullFace) {
		this(uv, texture, cullFace, VariantSettings.Rotation.R0, -1);
	}

	/**
	 * Create a new face builder with a given UV and texture key..
	 *
	 * @param uv The UV area of a texture to use for this face, given as a {@link Vector4i}.
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 */
	public FaceBuilder(Vector4i uv, TextureKey texture) {
		this(uv, texture, Face.NONE, VariantSettings.Rotation.R0, -1);
	}

	/**
	 * Create a new face builder with a given UV, texture key, cull face, rotation and tint index.
	 *
	 * @param x1 The X-coordinate of the first vertex (U).
	 * @param y1 The Y-coordinate of the first vertex (U).
	 * @param x2 The X-coordinate of the second vertex (V).
	 * @param y2 The Y-coordinate of the second vertex (V).
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 * @param cullFace If specified, this face need not render if a block is directly adjacent to the given one.
	 * @param rotation The rotation of this texture (quarter-turns only).
	 * @param tintIndex The tint index for this face, if applicable.
	 */
	public FaceBuilder(int x1, int y1, int x2, int y2, TextureKey texture, Face cullFace, VariantSettings.Rotation rotation, int tintIndex) {
		this(new Vector4i(x1, y1, x2, y2), texture, cullFace, rotation, tintIndex);
	}

	/**
	 * Create a new face builder with a given UV, texture key, cull face and rotation.
	 *
	 * @param x1 The X-coordinate of the first vertex (U).
	 * @param y1 The Y-coordinate of the first vertex (U).
	 * @param x2 The X-coordinate of the second vertex (V).
	 * @param y2 The Y-coordinate of the second vertex (V).
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 * @param cullFace If specified, this face need not render if a block is directly adjacent to the given one.
	 * @param rotation The rotation of this texture (quarter-turns only).
	 */
	public FaceBuilder(int x1, int y1, int x2, int y2, TextureKey texture, Face cullFace, VariantSettings.Rotation rotation) {
		this(x1, y1, x2, y2, texture, cullFace, rotation, -1);
	}

	/**
	 * Create a new face builder with a given UV, texture key and cull face.
	 *
	 * @param x1 The X-coordinate of the first vertex (U).
	 * @param y1 The Y-coordinate of the first vertex (U).
	 * @param x2 The X-coordinate of the second vertex (V).
	 * @param y2 The Y-coordinate of the second vertex (V).
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 * @param cullFace If specified, this face need not render if a block is directly adjacent to the given one.
	 */
	public FaceBuilder(int x1, int y1, int x2, int y2, TextureKey texture, Face cullFace) {
		this(x1, y1, x2, y2, texture, cullFace, VariantSettings.Rotation.R0, -1);
	}

	/**
	 * Create a new face builder with a given UV and texture key.
	 *
	 * @param x1 The X-coordinate of the first vertex (U).
	 * @param y1 The Y-coordinate of the first vertex (U).
	 * @param x2 The X-coordinate of the second vertex (V).
	 * @param y2 The Y-coordinate of the second vertex (V).
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 */
	public FaceBuilder(int x1, int y1, int x2, int y2, TextureKey texture) {
		this(x1, y1, x2, y2, texture, Face.NONE, VariantSettings.Rotation.R0, -1);
	}

	/**
	 * Create a new face builder with a given texture key.
	 * Defaults UV data to be based on the position of the element whose face is being built.
	 *
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 */
	public FaceBuilder(TextureKey texture) {
		this(new Vector4i(0), texture, Face.NONE, VariantSettings.Rotation.R0, -1);
	}

	public JsonObject build() {
		var face = new JsonObject();

		var uv = new JsonArray();
		uv.add(this.uv.x());
		uv.add(this.uv.y());
		uv.add(this.uv.z());
		uv.add(this.uv.w());
		face.add("uv", uv);

		face.addProperty("texture", "#" + texture.getName());

		if (cullFace != Face.NONE) {
			face.addProperty("cullface", cullFace.name().toLowerCase());
		}

		if (rotation != VariantSettings.Rotation.R0) {
			face.addProperty("rotation", rotation.name().substring(1));
		}

		if (this.tintIndex != -1) {
			face.addProperty("tintindex", tintIndex);
		}

		return face;
	}

	public enum Face {
		DOWN, UP, NORTH, SOUTH, WEST, EAST,
		/**
		 * Fallback value; do not use directly.
		 */
		NONE
	}
}
