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

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

/**
 * A mutable {@link QuadView} instance. The base interface for
 * {@link QuadEmitter} and for dynamic renders/mesh transforms.
 *
 * <p>Instances of {@link MutableQuadView} will practically always be
 * thread local and/or reused - do not retain references.
 *
 * <p>Only the renderer should implement or extend this interface.
 */
public interface MutableQuadView extends QuadView {
	/**
	 * Causes texture to appear with no rotation.
	 * Pass in bakeFlags parameter to {@link #spriteBake(Sprite, int)}.
	 */
	int BAKE_ROTATE_NONE = 0;

	/**
	 * Causes texture to appear rotated 90 deg. clockwise relative to nominal face.
	 * Pass in bakeFlags parameter to {@link #spriteBake(Sprite, int)}.
	 */
	int BAKE_ROTATE_90 = 1;

	/**
	 * Causes texture to appear rotated 180 deg. relative to nominal face.
	 * Pass in bakeFlags parameter to {@link #spriteBake(Sprite, int)}.
	 */
	int BAKE_ROTATE_180 = 2;

	/**
	 * Causes texture to appear rotated 270 deg. clockwise relative to nominal face.
	 * Pass in bakeFlags parameter to {@link #spriteBake(Sprite, int)}.
	 */
	int BAKE_ROTATE_270 = 3;

	/**
	 * When enabled, texture coordinate are assigned based on vertex position.
	 * Any existing UV coordinates will be replaced.
	 * Pass in bakeFlags parameter to {@link #spriteBake(Sprite, int)}.
	 *
	 * <p>UV lock always derives texture coordinates based on nominal face, even
	 * when the quad is not co-planar with that face, and the result is
	 * the same as if the quad were projected onto the nominal face, which
	 * is usually the desired result.
	 */
	int BAKE_LOCK_UV = 4;

	/**
	 * When set, U texture coordinates for the given sprite are
	 * flipped as part of baking. Can be useful for some randomization
	 * and texture mapping scenarios. Results are different from what
	 * can be obtained via rotation and both can be applied.
	 * Pass in bakeFlags parameter to {@link #spriteBake(Sprite, int)}.
	 */
	int BAKE_FLIP_U = 8;

	/**
	 * Same as {@link #BAKE_FLIP_U} but for V coordinate.
	 */
	int BAKE_FLIP_V = 16;

	/**
	 * UV coordinates by default are assumed to be 0-16 scale for consistency
	 * with conventional Minecraft model format. This is scaled to 0-1 during
	 * baking before interpolation. Model loaders that already have 0-1 coordinates
	 * can avoid wasteful multiplication/division by passing 0-1 coordinates directly.
	 * Pass in bakeFlags parameter to {@link #spriteBake(Sprite, int)}.
	 */
	int BAKE_NORMALIZED = 32;

	/**
	 * Sets the geometric vertex position for the given vertex,
	 * relative to block origin, (0,0,0). Minecraft rendering is designed
	 * for models that fit within a single block space and is recommended
	 * that coordinates remain in the 0-1 range, with multi-block meshes
	 * split into multiple per-block models.
	 */
	MutableQuadView pos(int vertexIndex, float x, float y, float z);

	/**
	 * Same as {@link #pos(int, float, float, float)} but accepts vector type.
	 */
	default MutableQuadView pos(int vertexIndex, Vector3f vec) {
		return pos(vertexIndex, vec.x(), vec.y(), vec.z());
	}

	/**
	 * Set vertex color.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MutableQuadView color(int vertexIndex, int color) {
		return spriteColor(vertexIndex, 0, color);
	}

	/**
	 * Convenience: set vertex color for all vertices at once.
	 */
	default MutableQuadView color(int c0, int c1, int c2, int c3) {
		color(0, c0);
		color(1, c1);
		color(2, c2);
		color(3, c3);
		return this;
	}

	/**
	 * Set texture coordinates.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MutableQuadView uv(int vertexIndex, float u, float v) {
		return sprite(vertexIndex, 0, u, v);
	}

	/**
	 * Set texture coordinates.
	 *
	 * <p>Only use this function if you already have a {@link Vec2f}.
	 * Otherwise, see {@link MutableQuadView#uv(int, float, float)}.
	 */
	default MutableQuadView uv(int vertexIndex, Vec2f uv) {
		return uv(vertexIndex, uv.x, uv.y);
	}

	/**
	 * Assigns sprite atlas u,v coordinates to this quad for the given sprite.
	 * Can handle UV locking, rotation, interpolation, etc. Control this behavior
	 * by passing additive combinations of the BAKE_ flags defined in this interface.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MutableQuadView spriteBake(Sprite sprite, int bakeFlags) {
		return spriteBake(0, sprite, bakeFlags);
	}

	/**
	 * Accept vanilla lightmap values.  Input values will override lightmap values
	 * computed from world state if input values are higher. Exposed for completeness
	 * but some rendering implementations with non-standard lighting model may not honor it.
	 *
	 * <p>For emissive rendering, it is better to use {@link MaterialFinder#emissive(boolean)}.
	 */
	MutableQuadView lightmap(int vertexIndex, int lightmap);

	/**
	 * Convenience: set lightmap for all vertices at once.
	 *
	 * <p>For emissive rendering, it is better to use {@link MaterialFinder#emissive(boolean)}.
	 * See {@link #lightmap(int, int)}.
	 */
	default MutableQuadView lightmap(int b0, int b1, int b2, int b3) {
		lightmap(0, b0);
		lightmap(1, b1);
		lightmap(2, b2);
		lightmap(3, b3);
		return this;
	}

	/**
	 * Adds a vertex normal. Models that have per-vertex
	 * normals should include them to get correct lighting when it matters.
	 * Computed face normal is used when no vertex normal is provided.
	 *
	 * <p>{@link Renderer} implementations should honor vertex normals for
	 * diffuse lighting - modifying vertex color(s) or packing normals in the vertex
	 * buffer as appropriate for the rendering method/vertex format in effect.
	 */
	MutableQuadView normal(int vertexIndex, float x, float y, float z);

	/**
	 * Same as {@link #normal(int, float, float, float)} but accepts vector type.
	 */
	default MutableQuadView normal(int vertexIndex, Vector3f vec) {
		return normal(vertexIndex, vec.x(), vec.y(), vec.z());
	}

	/**
	 * If non-null, quad is coplanar with a block face which, if known, simplifies
	 * or shortcuts geometric analysis that might otherwise be needed.
	 * Set to null if quad is not coplanar or if this is not known.
	 * Also controls face culling during block rendering.
	 *
	 * <p>Null by default.
	 *
	 * <p>When called with a non-null value, also sets {@link #nominalFace(Direction)}
	 * to the same value.
	 *
	 * <p>This is different from the value reported by {@link BakedQuad#getFace()}. That value
	 * is computed based on face geometry and must be non-null in vanilla quads.
	 * That computed value is returned by {@link #lightFace()}.
	 */
	@Nullable
	MutableQuadView cullFace(@Nullable Direction face);

	/**
	 * Provides a hint to renderer about the facing of this quad. Not required,
	 * but if provided can shortcut some geometric analysis if the quad is parallel to a block face.
	 * Should be the expected value of {@link #lightFace()}. Value will be confirmed
	 * and if invalid the correct light face will be calculated.
	 *
	 * <p>Null by default, and set automatically by {@link #cullFace()}.
	 *
	 * <p>Models may also find this useful as the face for texture UV locking and rotation semantics.
	 *
	 * <p>Note: This value is not persisted independently when the quad is encoded.
	 * When reading encoded quads, this value will always be the same as {@link #lightFace()}.
	 */
	@Nullable
	MutableQuadView nominalFace(Direction face);

	/**
	 * Assigns a different material to this quad. Useful for transformation of
	 * existing meshes because lighting and texture blending are controlled by material.
	 */
	MutableQuadView material(RenderMaterial material);

	/**
	 * Value functions identically to {@link BakedQuad#getColorIndex()} and is
	 * used by renderer / model builder in same way. Default value is -1.
	 */
	MutableQuadView colorIndex(int colorIndex);

	/**
	 * Encodes an integer tag with this quad that can later be retrieved via
	 * {@link QuadView#tag()}.  Useful for models that want to perform conditional
	 * transformation or filtering on static meshes.
	 */
	MutableQuadView tag(int tag);

	/**
	 * Enables bulk vertex data transfer using the standard Minecraft vertex formats.
	 * This method should be performant whenever caller's vertex representation makes it feasible.
	 *
	 * <p>Use {@link #fromVanilla(BakedQuad, RenderMaterial, Direction) the other overload} which has better encapsulation
	 * unless you have a specific reason to use this one.
	 *
	 * <p>Calling this method does not emit the quad.
	 *
	 * @apiNote The default implementation will be removed in the next breaking release.
	 */
	default MutableQuadView fromVanilla(int[] quadData, int startIndex) {
		return fromVanilla(quadData, startIndex, false);
	}

	/**
	 * Enables bulk vertex data transfer using the standard Minecraft vertex formats.
	 * This method should be performant whenever caller's vertex representation makes it feasible.
	 *
	 * <p>Calling this method does not emit the quad.
	 */
	MutableQuadView fromVanilla(BakedQuad quad, RenderMaterial material, Direction cullFace);

	/**
	 * @deprecated Use {@link #color(int, int)} instead.
	 */
	@Deprecated
	default MutableQuadView spriteColor(int vertexIndex, int spriteIndex, int color) {
		return color(vertexIndex, color);
	}

	/**
	 * @deprecated Use {@link #color(int, int, int, int)} instead.
	 */
	@Deprecated
	default MutableQuadView spriteColor(int spriteIndex, int c0, int c1, int c2, int c3) {
		color(c0, c1, c2, c3);
		return this;
	}

	/**
	 * @deprecated Use {@link #uv(int, float, float)} instead.
	 */
	@Deprecated
	default MutableQuadView sprite(int vertexIndex, int spriteIndex, float u, float v) {
		return uv(vertexIndex, u, v);
	}

	/**
	 * @deprecated Use {@link #uv(int, Vec2f)} instead.
	 */
	@Deprecated
	default MutableQuadView sprite(int vertexIndex, int spriteIndex, Vec2f uv) {
		return uv(vertexIndex, uv);
	}

	/**
	 * @deprecated Use {@link #spriteBake(Sprite, int)} instead.
	 */
	@Deprecated
	default MutableQuadView spriteBake(int spriteIndex, Sprite sprite, int bakeFlags) {
		return spriteBake(sprite, bakeFlags);
	}

	/**
	 * @deprecated Use {@link #fromVanilla(int[], int)} instead.
	 */
	@Deprecated
	default MutableQuadView fromVanilla(int[] quadData, int startIndex, boolean isItem) {
		return fromVanilla(quadData, startIndex);
	}
}
