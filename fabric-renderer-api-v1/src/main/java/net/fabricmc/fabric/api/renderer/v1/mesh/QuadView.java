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

package net.fabricmc.fabric.api.renderer.v1.mesh;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

/**
 * Interface for reading quad data encoded by {@link MeshBuilder}.
 * Enables models to do analysis, re-texturing or translation without knowing the
 * renderer's vertex formats and without retaining redundant information.
 *
 * <p>Only the renderer should implement or extend this interface.
 */
public interface QuadView {
	/** Count of integers in a conventional (un-modded) block or item vertex. */
	int VANILLA_VERTEX_STRIDE = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeInteger();

	/** Count of integers in a conventional (un-modded) block or item quad. */
	int VANILLA_QUAD_STRIDE = VANILLA_VERTEX_STRIDE * 4;

	/**
	 * Reads baked vertex data and outputs standard baked quad
	 * vertex data in the given array and location.
	 *
	 * @param spriteIndex The sprite to be used for the quad.
	 * Behavior for values &gt; 0 is currently undefined.
	 *
	 * @param target Target array for the baked quad data.
	 *
	 * @param targetIndex Starting position in target array - array must have
	 * at least 28 elements available at this index.
	 *
	 * @param isItem If true, will output vertex normals. Otherwise will output
	 * lightmaps, per Minecraft vertex formats for baked models.
	 */
	void toVanilla(int spriteIndex, int[] target, int targetIndex, boolean isItem);

	/**
	 * Extracts all quad properties except material to the given {@link MutableQuadView} instance.
	 * Must be used before calling {link QuadEmitter#emit()} on the target instance.
	 * Meant for re-texturing, analysis and static transformation use cases.
	 */
	void copyTo(MutableQuadView target);

	/**
	 * Retrieves the material serialized with the quad.
	 */
	RenderMaterial material();

	/**
	 * Retrieves the quad color index serialized with the quad.
	 */
	int colorIndex();

	/**
	 * Equivalent to {@link BakedQuad#getFace()}. This is the face used for vanilla lighting
	 * calculations and will be the block face to which the quad is most closely aligned. Always
	 * the same as cull face for quads that are on a block face, but never null.
	 */
	Direction lightFace();

	/**
	 * If non-null, quad should not be rendered in-world if the
	 * opposite face of a neighbor block occludes it.
	 *
	 * @see MutableQuadView#cullFace(Direction)
	 */
	Direction cullFace();

	/**
	 * See {@link MutableQuadView#nominalFace(Direction)}.
	 */
	Direction nominalFace();

	/**
	 * Normal of the quad as implied by geometry. Will be invalid
	 * if quad vertices are not co-planar.  Typically computed lazily
	 * on demand and not encoded.
	 *
	 * <p>Not typically needed by models. Exposed to enable standard lighting
	 * utility functions for use by renderers.
	 */
	Vector3f faceNormal();

	/**
	 * Generates a new BakedQuad instance with texture
	 * coordinates and colors from the given sprite.
	 *
	 * @param spriteIndex The sprite to be used for the quad.
	 * Behavior for {@code spriteIndex > 0} is currently undefined.
	 *
	 * @param sprite  {@link MutableQuadView} does not serialize sprites
	 * so the sprite must be provided by the caller.
	 *
	 * @param isItem If true, will output vertex normals. Otherwise will output
	 * lightmaps, per Minecraft vertex formats for baked models.
	 *
	 * @return A new baked quad instance with the closest-available appearance
	 * supported by vanilla features. Will retain emissive light maps, for example,
	 * but the standard Minecraft renderer will not use them.
	 */
	default BakedQuad toBakedQuad(int spriteIndex, Sprite sprite, boolean isItem) {
		int[] vertexData = new int[VANILLA_QUAD_STRIDE];
		toVanilla(spriteIndex, vertexData, 0, isItem);
		return new BakedQuad(vertexData, colorIndex(), lightFace(), sprite, true /* TODO:20w09a check me */);
	}

	/**
	 * Retrieves the integer tag encoded with this quad via {@link MutableQuadView#tag(int)}.
	 * Will return zero if no tag was set.  For use by models.
	 */
	int tag();

	/**
	 * Pass a non-null target to avoid allocation - will be returned with values.
	 * Otherwise returns a new instance.
	 */
	Vector3f copyPos(int vertexIndex, Vector3f target);

	/**
	 * Convenience: access x, y, z by index 0-2.
	 */
	float posByIndex(int vertexIndex, int coordinateIndex);

	/**
	 * Geometric position, x coordinate.
	 */
	float x(int vertexIndex);

	/**
	 * Geometric position, y coordinate.
	 */
	float y(int vertexIndex);

	/**
	 * Geometric position, z coordinate.
	 */
	float z(int vertexIndex);

	/**
	 * If false, no vertex normal was provided.
	 * Lighting should use face normal in that case.
	 */
	boolean hasNormal(int vertexIndex);

	/**
	 * Pass a non-null target to avoid allocation - will be returned with values.
	 * Otherwise returns a new instance. Returns null if normal not present.
	 */
	Vector3f copyNormal(int vertexIndex, Vector3f target);

	/**
	 * Will return {@link Float#NaN} if normal not present.
	 */
	float normalX(int vertexIndex);

	/**
	 * Will return {@link Float#NaN} if normal not present.
	 */
	float normalY(int vertexIndex);

	/**
	 * Will return {@link Float#NaN} if normal not present.
	 */
	float normalZ(int vertexIndex);

	/**
	 * Minimum block brightness. Zero if not set.
	 */
	int lightmap(int vertexIndex);

	/**
	 * Retrieve vertex color.
	 */
	int spriteColor(int vertexIndex, int spriteIndex);

	/**
	 * Retrieve horizontal sprite atlas coordinates.
	 */
	float spriteU(int vertexIndex, int spriteIndex);

	/**
	 * Retrieve vertical sprite atlas coordinates.
	 */
	float spriteV(int vertexIndex, int spriteIndex);
}
