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

package net.fabricmc.fabric.impl.client.indigo.renderer.render;

import static net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper.AXIS_ALIGNED_FLAG;
import static net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper.LIGHT_FACE_FLAG;

import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;

/**
 * Base quad-rendering class for fallback and mesh consumers.
 * Has most of the actual buffer-time lighting and coloring logic.
 */
public abstract class AbstractQuadRenderer {
	protected final Function<RenderLayer, VertexConsumer> bufferFunc;
	protected final BlockRenderInfo blockInfo;
	protected final AoCalculator aoCalc;
	protected final QuadTransform transform;
	protected final Vec3f normalVec = new Vec3f();

	protected abstract Matrix4f matrix();

	protected abstract Matrix3f normalMatrix();

	protected abstract int overlay();

	AbstractQuadRenderer(BlockRenderInfo blockInfo, Function<RenderLayer, VertexConsumer> bufferFunc, AoCalculator aoCalc, QuadTransform transform) {
		this.blockInfo = blockInfo;
		this.bufferFunc = bufferFunc;
		this.aoCalc = aoCalc;
		this.transform = transform;
	}

	protected void renderQuad(MutableQuadViewImpl quad, boolean isVanilla) {
		if (!transform.transform(quad)) {
			return;
		}

		if (!blockInfo.shouldDrawFace(quad.cullFace())) {
			return;
		}

		tessellateQuad(quad, 0, isVanilla);
	}

	/**
	 * Determines color index and render layer, then routes to appropriate
	 * tessellate routine based on material properties.
	 */
	private void tessellateQuad(MutableQuadViewImpl quad, int textureIndex, boolean isVanilla) {
		final RenderMaterialImpl.Value mat = quad.material();
		final int colorIndex = mat.disableColorIndex(textureIndex) ? -1 : quad.colorIndex();
		final RenderLayer renderLayer = blockInfo.effectiveRenderLayer(mat.blendMode(textureIndex));

		if (blockInfo.defaultAo && !mat.disableAo(textureIndex)) {
			// needs to happen before offsets are applied
			aoCalc.compute(quad, isVanilla);

			if (mat.emissive(textureIndex)) {
				tessellateSmoothEmissive(quad, renderLayer, colorIndex);
			} else {
				tessellateSmooth(quad, renderLayer, colorIndex);
			}
		} else {
			if (mat.emissive(textureIndex)) {
				tessellateFlatEmissive(quad, renderLayer, colorIndex);
			} else {
				tessellateFlat(quad, renderLayer, colorIndex);
			}
		}
	}

	/** handles block color and red-blue swizzle, common to all renders. */
	private void colorizeQuad(MutableQuadViewImpl q, int blockColorIndex) {
		if (blockColorIndex == -1) {
			for (int i = 0; i < 4; i++) {
				q.spriteColor(i, 0, ColorHelper.swapRedBlueIfNeeded(q.spriteColor(i, 0)));
			}
		} else {
			final int blockColor = blockInfo.blockColor(blockColorIndex);

			for (int i = 0; i < 4; i++) {
				q.spriteColor(i, 0, ColorHelper.swapRedBlueIfNeeded(ColorHelper.multiplyColor(blockColor, q.spriteColor(i, 0))));
			}
		}
	}

	/** final output step, common to all renders. */
	private void bufferQuad(MutableQuadViewImpl quad, RenderLayer renderLayer) {
		bufferQuad(bufferFunc.apply(renderLayer), quad, matrix(), overlay(), normalMatrix(), normalVec);
	}

	public static void bufferQuad(VertexConsumer buff, MutableQuadViewImpl quad, Matrix4f matrix, int overlay, Matrix3f normalMatrix, Vec3f normalVec) {
		final boolean useNormals = quad.hasVertexNormals();

		if (useNormals) {
			quad.populateMissingNormals();
		} else {
			final Vec3f faceNormal = quad.faceNormal();
			normalVec.set(faceNormal.getX(), faceNormal.getY(), faceNormal.getZ());
			normalVec.transform(normalMatrix);
		}

		for (int i = 0; i < 4; i++) {
			buff.vertex(matrix, quad.x(i), quad.y(i), quad.z(i));
			final int color = quad.spriteColor(i, 0);
			buff.color(color & 0xFF, (color >> 8) & 0xFF, (color >> 16) & 0xFF, (color >> 24) & 0xFF);
			buff.texture(quad.spriteU(i, 0), quad.spriteV(i, 0));
			buff.overlay(overlay);
			buff.light(quad.lightmap(i));

			if (useNormals) {
				normalVec.set(quad.normalX(i), quad.normalY(i), quad.normalZ(i));
				normalVec.transform(normalMatrix);
			}

			buff.normal(normalVec.getX(), normalVec.getY(), normalVec.getZ());
			buff.next();
		}
	}

	// routines below have a bit of copy-paste code reuse to avoid conditional execution inside a hot loop

	/** for non-emissive mesh quads and all fallback quads with smooth lighting. */
	protected void tessellateSmooth(MutableQuadViewImpl q, RenderLayer renderLayer, int blockColorIndex) {
		colorizeQuad(q, blockColorIndex);

		for (int i = 0; i < 4; i++) {
			q.spriteColor(i, 0, ColorHelper.multiplyRGB(q.spriteColor(i, 0), aoCalc.ao[i]));
			q.lightmap(i, ColorHelper.maxBrightness(q.lightmap(i), aoCalc.light[i]));
		}

		bufferQuad(q, renderLayer);
	}

	/** for emissive mesh quads with smooth lighting. */
	protected void tessellateSmoothEmissive(MutableQuadViewImpl q, RenderLayer renderLayer, int blockColorIndex) {
		colorizeQuad(q, blockColorIndex);

		for (int i = 0; i < 4; i++) {
			q.spriteColor(i, 0, ColorHelper.multiplyRGB(q.spriteColor(i, 0), aoCalc.ao[i]));
			q.lightmap(i, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		}

		bufferQuad(q, renderLayer);
	}

	/** for non-emissive mesh quads and all fallback quads with flat lighting. */
	protected void tessellateFlat(MutableQuadViewImpl quad, RenderLayer renderLayer, int blockColorIndex) {
		colorizeQuad(quad, blockColorIndex);
		shadeFlatQuad(quad);

		final int brightness = flatBrightness(quad, blockInfo.blockState, blockInfo.blockPos);

		for (int i = 0; i < 4; i++) {
			quad.lightmap(i, ColorHelper.maxBrightness(quad.lightmap(i), brightness));
		}

		bufferQuad(quad, renderLayer);
	}

	/** for emissive mesh quads with flat lighting. */
	protected void tessellateFlatEmissive(MutableQuadViewImpl quad, RenderLayer renderLayer, int blockColorIndex) {
		colorizeQuad(quad, blockColorIndex);
		shadeFlatQuad(quad);

		for (int i = 0; i < 4; i++) {
			quad.lightmap(i, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		}

		bufferQuad(quad, renderLayer);
	}

	private final BlockPos.Mutable mpos = new BlockPos.Mutable();

	/**
	 * Handles geometry-based check for using self brightness or neighbor brightness.
	 * That logic only applies in flat lighting.
	 */
	int flatBrightness(MutableQuadViewImpl quad, BlockState blockState, BlockPos pos) {
		mpos.set(pos);

		// To mirror Vanilla's behavior, if the face has a cull-face, always sample the light value
		// offset in that direction. See net.minecraft.client.render.block.BlockModelRenderer.renderQuadsFlat
		// for reference.
		if (quad.cullFace() != null) {
			mpos.move(quad.cullFace());
		} else {
			final int flags = quad.geometryFlags();

			if ((flags & LIGHT_FACE_FLAG) != 0 || ((flags & AXIS_ALIGNED_FLAG) != 0 && blockState.isFullCube(blockInfo.blockView, pos))) {
				mpos.move(quad.lightFace());
			}
		}

		// Unfortunately cannot use brightness cache here unless we implement one specifically for flat lighting. See #329
		return WorldRenderer.getLightmapCoordinates(blockInfo.blockView, blockState, mpos);
	}

	/**
	 * Starting in 1.16 flat shading uses dimension-specific diffuse factors that can be < 1.0
	 * even for un-shaded quads. These are also applied with AO shading but that is done in AO calculator.
	 */
	private void shadeFlatQuad(MutableQuadViewImpl quad) {
		if (quad.hasVertexNormals()) {
			// Quads that have vertex normals need to be shaded using interpolation - vanilla can't
			// handle them. Generally only applies to modded models.
			final float faceShade = blockInfo.blockView.getBrightness(quad.lightFace(), quad.hasShade());

			for (int i = 0; i < 4; i++) {
				quad.spriteColor(i, 0, ColorHelper.multiplyRGB(quad.spriteColor(i, 0), vertexShade(quad, i, faceShade)));
			}
		} else {
			final float diffuseShade = blockInfo.blockView.getBrightness(quad.lightFace(), quad.hasShade());

			if (diffuseShade != 1.0f) {
				for (int i = 0; i < 4; i++) {
					quad.spriteColor(i, 0, ColorHelper.multiplyRGB(quad.spriteColor(i, 0), diffuseShade));
				}
			}
		}
	}

	private float vertexShade(MutableQuadViewImpl quad, int vertexIndex, float faceShade) {
		return quad.hasNormal(vertexIndex) ? normalShade(quad.normalX(vertexIndex), quad.normalY(vertexIndex), quad.normalZ(vertexIndex), quad.hasShade()) : faceShade;
	}

	/**
	 * Finds mean of per-face shading factors weighted by normal components.
	 * Not how light actually works but the vanilla diffuse shading model is a hack to start with
	 * and this gives reasonable results for non-cubic surfaces in a vanilla-style renderer.
	 */
	private float normalShade(float normalX, float normalY, float normalZ, boolean hasShade) {
		float sum = 0;
		float div = 0;

		if (normalX > 0) {
			sum += normalX * blockInfo.blockView.getBrightness(Direction.EAST, hasShade);
			div += normalX;
		} else if (normalX < 0) {
			sum += -normalX * blockInfo.blockView.getBrightness(Direction.WEST, hasShade);
			div -= normalX;
		}

		if (normalY > 0) {
			sum += normalY * blockInfo.blockView.getBrightness(Direction.UP, hasShade);
			div += normalY;
		} else if (normalY < 0) {
			sum += -normalY * blockInfo.blockView.getBrightness(Direction.DOWN, hasShade);
			div -= normalY;
		}

		if (normalZ > 0) {
			sum += normalZ * blockInfo.blockView.getBrightness(Direction.SOUTH, hasShade);
			div += normalZ;
		} else if (normalZ < 0) {
			sum += -normalZ * blockInfo.blockView.getBrightness(Direction.NORTH, hasShade);
			div -= normalZ;
		}

		return sum / div;
	}
}
