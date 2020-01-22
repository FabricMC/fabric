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

package net.fabricmc.fabric.impl.client.indigo.renderer.helper;

import java.nio.ByteOrder;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;

/**
 * Static routines of general utility for renderer implementations.
 * Renderers are not required to use these helpers, but they were
 * designed to be usable without the default renderer.
 */
public abstract class ColorHelper {
	private ColorHelper() { }

	/**
	 * Implement on quads to use methods that require it.
	 * Allows for much cleaner method signatures.
	 */
	public interface ShadeableQuad extends MutableQuadView {
		boolean isFaceAligned();

		boolean needsDiffuseShading(int textureIndex);
	}

	/** Same as vanilla values. */
	private static final float[] FACE_SHADE_FACTORS = { 0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F };

	private static final Int2IntFunction colorSwapper = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? color -> ((color & 0xFF00FF00) | ((color & 0x00FF0000) >> 16) | ((color & 0xFF) << 16)) : color -> color;

	/**
	 * Swaps red blue order if needed to match GPU expectations for color component order.
	 */
	public static int swapRedBlueIfNeeded(int color) {
		return colorSwapper.applyAsInt(color);
	}

	/** Component-wise multiply. Components need to be in same order in both inputs! */
	public static int multiplyColor(int color1, int color2) {
		if (color1 == -1) {
			return color2;
		} else if (color2 == -1) {
			return color1;
		}

		int alpha = ((color1 >> 24) & 0xFF) * ((color2 >> 24) & 0xFF) / 0xFF;
		int red = ((color1 >> 16) & 0xFF) * ((color2 >> 16) & 0xFF) / 0xFF;
		int green = ((color1 >> 8) & 0xFF) * ((color2 >> 8) & 0xFF) / 0xFF;
		int blue = (color1 & 0xFF) * (color2 & 0xFF) / 0xFF;

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/** Multiplies three lowest components by shade. High byte (usually alpha) unchanged. */
	public static int multiplyRGB(int color, float shade) {
		int alpha = ((color >> 24) & 0xFF);
		int red = (int) (((color >> 16) & 0xFF) * shade);
		int green = (int) (((color >> 8) & 0xFF) * shade);
		int blue = (int) ((color & 0xFF) * shade);

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
	 * Same results as {@link BakedQuadFactory#method_3456(Direction)}.
	 */
	public static float diffuseShade(Direction direction) {
		return FACE_SHADE_FACTORS[direction.getId()];
	}

	/**
	 * Formula mimics vanilla lighting for plane-aligned quads and
	 * is vaguely consistent with Phong lighting ambient + diffuse for others.
	 */
	public static float normalShade(float normalX, float normalY, float normalZ) {
		return Math.min(0.5f + Math.abs(normalX) * 0.1f + (normalY > 0 ? 0.5f * normalY : 0) + Math.abs(normalZ) * 0.3f, 1f);
	}

	public static float normalShade(Vector3f normal) {
		return normalShade(normal.getX(), normal.getY(), normal.getZ());
	}

	/**
	 * @see #diffuseShade
	 */
	public static float vertexShade(ShadeableQuad q, int vertexIndex, float faceShade) {
		return q.hasNormal(vertexIndex) ? normalShade(q.normalX(vertexIndex), q.normalY(vertexIndex), q.normalZ(vertexIndex)) : faceShade;
	}

	/**
	 * Returns {@link #diffuseShade(Direction)} if quad is aligned to light face,
	 * otherwise uses face normal and {@link #normalShade}.
	 */
	public static float faceShade(ShadeableQuad quad) {
		return quad.isFaceAligned() ? diffuseShade(quad.lightFace()) : normalShade(quad.faceNormal());
	}

	@FunctionalInterface
	private interface VertexLighter {
		void shade(ShadeableQuad quad, int vertexIndex, float shade);
	}

	private static VertexLighter[] VERTEX_LIGHTERS = new VertexLighter[8];

	static {
		VERTEX_LIGHTERS[0b000] = (q, i, s) -> { };
		VERTEX_LIGHTERS[0b001] = (q, i, s) -> q.spriteColor(i, 0, multiplyRGB(q.spriteColor(i, 0), s));
		VERTEX_LIGHTERS[0b010] = (q, i, s) -> q.spriteColor(i, 1, multiplyRGB(q.spriteColor(i, 1), s));
		VERTEX_LIGHTERS[0b011] = (q, i, s) -> q.spriteColor(i, 0, multiplyRGB(q.spriteColor(i, 0), s)).spriteColor(i, 1, multiplyRGB(q.spriteColor(i, 1), s));
		VERTEX_LIGHTERS[0b100] = (q, i, s) -> q.spriteColor(i, 2, multiplyRGB(q.spriteColor(i, 2), s));
		VERTEX_LIGHTERS[0b101] = (q, i, s) -> q.spriteColor(i, 0, multiplyRGB(q.spriteColor(i, 0), s)).spriteColor(i, 2, multiplyRGB(q.spriteColor(i, 2), s));
		VERTEX_LIGHTERS[0b110] = (q, i, s) -> q.spriteColor(i, 1, multiplyRGB(q.spriteColor(i, 1), s)).spriteColor(i, 2, multiplyRGB(q.spriteColor(i, 2), s));
		VERTEX_LIGHTERS[0b111] = (q, i, s) -> q.spriteColor(i, 0, multiplyRGB(q.spriteColor(i, 0), s)).spriteColor(i, 1, multiplyRGB(q.spriteColor(i, 1), s)).spriteColor(i, 2, multiplyRGB(q.spriteColor(i, 2), s));
	}

	/**
	 * Honors vertex normals and uses non-cubic face normals for non-cubic quads.
	 *
	 * @param quad Quad to be shaded/unshaded.
	 *
	 * @param undo If true, will reverse prior application.  Does not check that
	 * prior application actually happened.  Use to "unbake" a quad.
	 * Some drift of colors may occur due to floating-point precision error.
	 */
	public static void applyDiffuseShading(ShadeableQuad quad, boolean undo) {
		final float faceShade = faceShade(quad);
		int i = quad.needsDiffuseShading(0) ? 1 : 0;

		if (quad.needsDiffuseShading(1)) {
			i |= 2;
		}

		if (quad.needsDiffuseShading(2)) {
			i |= 4;
		}

		if (i == 0) {
			return;
		}

		final VertexLighter shader = VERTEX_LIGHTERS[i];

		for (int j = 0; j < 4; j++) {
			final float vertexShade = vertexShade(quad, j, faceShade);
			shader.shade(quad, j, undo ? 1f / vertexShade : vertexShade);
		}
	}

	/**
	 * Component-wise max.
	 */
	public static int maxBrightness(int b0, int b1) {
		if (b0 == 0) return b1;
		if (b1 == 0) return b0;

		return Math.max(b0 & 0xFFFF, b1 & 0xFFFF) | Math.max(b0 & 0xFFFF0000, b1 & 0xFFFF0000);
	}
}
