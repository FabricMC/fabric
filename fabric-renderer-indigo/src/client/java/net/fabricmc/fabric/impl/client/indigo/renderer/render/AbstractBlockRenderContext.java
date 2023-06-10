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

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;

public abstract class AbstractBlockRenderContext extends AbstractRenderContext {
	protected final BlockRenderInfo blockInfo = new BlockRenderInfo();
	protected final AoCalculator aoCalc;

	private final MutableQuadViewImpl editorQuad = new MutableQuadViewImpl() {
		{
			data = new int[EncodingFormat.TOTAL_STRIDE];
			clear();
		}

		@Override
		public void emitDirectly() {
			renderQuad(this, false);
		}
	};

	private final BakedModelConsumerImpl vanillaModelConsumer = new BakedModelConsumerImpl();

	private final BlockPos.Mutable lightPos = new BlockPos.Mutable();

	protected AbstractBlockRenderContext() {
		aoCalc = createAoCalc(blockInfo);
	}

	protected abstract AoCalculator createAoCalc(BlockRenderInfo blockInfo);

	protected abstract VertexConsumer getVertexConsumer(RenderLayer layer);

	@Override
	public QuadEmitter getEmitter() {
		editorQuad.clear();
		return editorQuad;
	}

	@Override
	public BakedModelConsumer bakedModelConsumer() {
		return vanillaModelConsumer;
	}

	private void renderQuad(MutableQuadViewImpl quad, boolean isVanilla) {
		if (!transform(quad)) {
			return;
		}

		if (!blockInfo.shouldDrawFace(quad.cullFace())) {
			return;
		}

		final RenderMaterial mat = quad.material();
		final int colorIndex = mat.disableColorIndex() ? -1 : quad.colorIndex();
		final TriState aoMode = mat.ambientOcclusion();
		final boolean ao = blockInfo.useAo && (aoMode == TriState.TRUE || (aoMode == TriState.DEFAULT && blockInfo.defaultAo));
		final boolean emissive = mat.emissive();
		final VertexConsumer vertexConsumer = getVertexConsumer(blockInfo.effectiveRenderLayer(mat.blendMode()));

		colorizeQuad(quad, colorIndex);
		shadeQuad(quad, isVanilla, ao, emissive);
		bufferQuad(quad, vertexConsumer);
	}

	/** handles block color, common to all renders. */
	private void colorizeQuad(MutableQuadViewImpl quad, int colorIndex) {
		if (colorIndex != -1) {
			final int blockColor = blockInfo.blockColor(colorIndex);

			for (int i = 0; i < 4; i++) {
				quad.color(i, ColorHelper.multiplyColor(blockColor, quad.color(i)));
			}
		}
	}

	private void shadeQuad(MutableQuadViewImpl quad, boolean isVanilla, boolean ao, boolean emissive) {
		// routines below have a bit of copy-paste code reuse to avoid conditional execution inside a hot loop
		if (ao) {
			aoCalc.compute(quad, isVanilla);

			if (emissive) {
				for (int i = 0; i < 4; i++) {
					quad.color(i, ColorHelper.multiplyRGB(quad.color(i), aoCalc.ao[i]));
					quad.lightmap(i, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				}
			} else {
				for (int i = 0; i < 4; i++) {
					quad.color(i, ColorHelper.multiplyRGB(quad.color(i), aoCalc.ao[i]));
					quad.lightmap(i, ColorHelper.maxBrightness(quad.lightmap(i), aoCalc.light[i]));
				}
			}
		} else {
			shadeFlatQuad(quad);

			if (emissive) {
				for (int i = 0; i < 4; i++) {
					quad.lightmap(i, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				}
			} else {
				final int brightness = flatBrightness(quad, blockInfo.blockState, blockInfo.blockPos);

				for (int i = 0; i < 4; i++) {
					quad.lightmap(i, ColorHelper.maxBrightness(quad.lightmap(i), brightness));
				}
			}
		}
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
				quad.color(i, ColorHelper.multiplyRGB(quad.color(i), vertexShade(quad, i, faceShade)));
			}
		} else {
			final float diffuseShade = blockInfo.blockView.getBrightness(quad.lightFace(), quad.hasShade());

			if (diffuseShade != 1.0f) {
				for (int i = 0; i < 4; i++) {
					quad.color(i, ColorHelper.multiplyRGB(quad.color(i), diffuseShade));
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

	/**
	 * Handles geometry-based check for using self brightness or neighbor brightness.
	 * That logic only applies in flat lighting.
	 */
	private int flatBrightness(MutableQuadViewImpl quad, BlockState blockState, BlockPos pos) {
		lightPos.set(pos);

		// To mirror Vanilla's behavior, if the face has a cull-face, always sample the light value
		// offset in that direction. See net.minecraft.client.render.block.BlockModelRenderer.renderQuadsFlat
		// for reference.
		if (quad.cullFace() != null) {
			lightPos.move(quad.cullFace());
		} else {
			final int flags = quad.geometryFlags();

			if ((flags & LIGHT_FACE_FLAG) != 0 || ((flags & AXIS_ALIGNED_FLAG) != 0 && blockState.isFullCube(blockInfo.blockView, pos))) {
				lightPos.move(quad.lightFace());
			}
		}

		// Unfortunately cannot use brightness cache here unless we implement one specifically for flat lighting. See #329
		return WorldRenderer.getLightmapCoordinates(blockInfo.blockView, blockState, lightPos);
	}

	/**
	 * Consumer for vanilla baked models. Generally intended to give visual results matching a vanilla render,
	 * however there could be subtle (and desirable) lighting variations so is good to be able to render
	 * everything consistently.
	 *
	 * <p>Also, the API allows multi-part models that hold multiple vanilla models to render them without
	 * combining quad lists, but the vanilla logic only handles one model per block. To route all of
	 * them through vanilla logic would require additional hooks.
	 */
	private class BakedModelConsumerImpl implements BakedModelConsumer {
		private static final RenderMaterial MATERIAL_SHADED = IndigoRenderer.INSTANCE.materialFinder().find();
		private static final RenderMaterial MATERIAL_FLAT = IndigoRenderer.INSTANCE.materialFinder().ambientOcclusion(TriState.FALSE).find();

		private final MutableQuadViewImpl editorQuad = new MutableQuadViewImpl() {
			{
				data = new int[EncodingFormat.TOTAL_STRIDE];
				clear();
			}

			@Override
			public void emitDirectly() {
				renderQuad(this, true);
			}
		};

		@Override
		public void accept(BakedModel model) {
			accept(model, blockInfo.blockState);
		}

		@Override
		public void accept(BakedModel model, @Nullable BlockState state) {
			MutableQuadViewImpl editorQuad = this.editorQuad;
			final RenderMaterial defaultMaterial = model.useAmbientOcclusion() ? MATERIAL_SHADED : MATERIAL_FLAT;

			for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
				final Direction cullFace = ModelHelper.faceFromIndex(i);
				final List<BakedQuad> quads = model.getQuads(state, cullFace, blockInfo.randomSupplier.get());
				final int count = quads.size();

				for (int j = 0; j < count; j++) {
					final BakedQuad q = quads.get(j);
					editorQuad.fromVanilla(q, defaultMaterial, cullFace);
					// Call renderQuad directly instead of emit for efficiency
					renderQuad(editorQuad, true);
				}
			}

			// Do not clear the editorQuad since it is not accessible to API users.
		}
	}
}
