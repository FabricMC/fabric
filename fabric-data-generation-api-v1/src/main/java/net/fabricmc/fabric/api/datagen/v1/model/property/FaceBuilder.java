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

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector4i;

import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.util.math.Direction;

/**
 * Instantiate this class in order to provide a set of <code>faces</code> to be rendered for an element of a JSON model.
 */
public class FaceBuilder {
	private final TextureKey texture;
	private Vector4i uv = new Vector4i(0);
	@Nullable
	private Direction cullFace = null;
	private VariantSettings.Rotation rotation = VariantSettings.Rotation.R0;
	private int tintIndex = -1;

	/**
	 * Create a new face builder for the given texture key.
	 *
	 * @param texture A key corresponding to the texture to be applied for this element face.
	 */
	public FaceBuilder(TextureKey texture) {
		this.texture = texture;
	}

	public FaceBuilder(String textureKey) {
		this.texture = TextureKey.of(textureKey);
	}

	/**
	 * Specifies the area of the texture to use for the face being built. Defaults to the zero vector, in which case
	 * the UV is automatically determined based on the position of the element.
	 *
	 * @param uv The UV coordinates to use for this face texture, given as a {@link Vector4i}.
	 */
	public FaceBuilder withUv(Vector4i uv) {
		validateUv(uv);
		this.uv = uv;
		return this;
	}

	/**
	 * Specifies the area of the texture to use for the face being built, given in terms of a pair of 2D vectors.
	 * Defaults to the zero vector, in which case the UV is automatically determined based on the position of the element.
	 */
	public FaceBuilder withUv(Vector2i u, Vector2i v) {
		return withUv(new Vector4i(u, v.x(), v.y()));
	}

	/**
	 * Specifies the area of the texture to use for the face being built, given in terms of individual vector components.
	 * Defaults to the zero vector, in which case the UV is automatically determined based on the position of the element.
	 */
	public FaceBuilder withUv(int x1, int y1, int x2, int y2) {
		return withUv(new Vector4i(x1, y1, x2, y2));
	}

	/**
	 * Specifies whether this face need not be rendered when there is a block adjacent to it in the specified position.
	 *
	 * @param cullFace The position from which an adjacent block should cull this face, or <code>null</code> to never hide (default).
	 */
	public FaceBuilder withCulling(@Nullable Direction cullFace) {
		this.cullFace = cullFace;
		return this;
	}

	/**
	 * Specifies a texture rotation for the face being built. Only accepts quarter-turns.
	 */
	public FaceBuilder withRotation(VariantSettings.Rotation rotation) {
		this.rotation = rotation;
		return this;
	}

	/**
	 * Specified a tint index for this face. Tint indexes are hardcoded into a respective item/block and correspond to
	 * some colour to be applied to a given face texture as defined in its class. Defaults to -1 (no tint index).
	 */
	public FaceBuilder withTintIndex(int tintIndex) {
		this.tintIndex = tintIndex;
		return this;
	}

	private void validateUv(Vector4i uv) {
		int[] components = {uv.x, uv.y, uv.z, uv.w};

		for (int c : components) {
			Preconditions.checkArgument(c >= 0 && c <= 16, "Component out of range");
		}
	}

	@ApiStatus.Internal
	public JsonObject build() {
		var face = new JsonObject();

		var uv = new JsonArray();
		uv.add(this.uv.x());
		uv.add(this.uv.y());
		uv.add(this.uv.z());
		uv.add(this.uv.w());
		face.add("uv", uv);

		face.addProperty("texture", "#" + texture.getName());

		if (cullFace != null) {
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
}
