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

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl.Value;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;

/**
 * Consumer for vanilla baked models. Generally intended to give visual results matching a vanilla render,
 * however there could be subtle (and desirable) lighting variations so is good to be able to render
 * everything consistently.
 *
 * <p>Also, the API allows multi-part models that hold multiple vanilla models to render them without
 * combining quad lists, but the vanilla logic only handles one model per block. To route all of
 * them through vanilla logic would require additional hooks.
 *
 *  <p>Works by copying the quad data to an "editor" quad held in the instance,
 *  where all transformations are applied before buffering. Transformations should be
 *  the same as they would be in a vanilla render - the editor is serving mainly
 *  as a way to access vertex data without magical numbers. It also allows a consistent interface
 *  for downstream tesselation routines.
 *
 *  <p>Another difference from vanilla render is that all transformation happens before the
 *  vertex data is sent to the byte buffer.  Generally POJO array access will be faster than
 *  manipulating the data via NIO.
 */
public abstract class TerrainFallbackConsumer extends AbstractQuadRenderer implements Consumer<BakedModel> {
	private static Value MATERIAL_FLAT = (Value) IndigoRenderer.INSTANCE.materialFinder().disableAo(0, true).find();
	private static Value MATERIAL_SHADED = (Value) IndigoRenderer.INSTANCE.materialFinder().find();

	private final int[] editorBuffer = new int[EncodingFormat.TOTAL_STRIDE];

	TerrainFallbackConsumer(BlockRenderInfo blockInfo, Function<RenderLayer, VertexConsumer> bufferFunc, AoCalculator aoCalc, QuadTransform transform) {
		super(blockInfo, bufferFunc, aoCalc, transform);
	}

	private final MutableQuadViewImpl editorQuad = new MutableQuadViewImpl() {
		{
			data = editorBuffer;
			material(MATERIAL_SHADED);
		}

		@Override
		public QuadEmitter emit() {
			// should not be called
			throw new UnsupportedOperationException("Fallback consumer does not support .emit()");
		}
	};

	@Override
	public void accept(BakedModel model) {
		final Supplier<Random> random = blockInfo.randomSupplier;
		final Value defaultMaterial = blockInfo.defaultAo && model.useAmbientOcclusion() ? MATERIAL_SHADED : MATERIAL_FLAT;
		final BlockState blockState = blockInfo.blockState;

		for (int i = 0; i < 6; i++) {
			Direction face = ModelHelper.faceFromIndex(i);
			List<BakedQuad> quads = model.getQuads(blockState, face, random.get());
			final int count = quads.size();

			if (count != 0) {
				for (int j = 0; j < count; j++) {
					BakedQuad q = quads.get(j);
					renderQuad(q, face, defaultMaterial);
				}
			}
		}

		List<BakedQuad> quads = model.getQuads(blockState, null, random.get());
		final int count = quads.size();

		if (count != 0) {
			for (int j = 0; j < count; j++) {
				BakedQuad q = quads.get(j);
				renderQuad(q, null, defaultMaterial);
			}
		}
	}

	private void renderQuad(BakedQuad quad, Direction cullFace, Value defaultMaterial) {
		final int[] vertexData = quad.getVertexData();

		if (!CompatibilityHelper.canRender(vertexData)) {
			return;
		}

		final MutableQuadViewImpl editorQuad = this.editorQuad;
		System.arraycopy(vertexData, 0, editorBuffer, EncodingFormat.HEADER_STRIDE, EncodingFormat.QUAD_STRIDE);
		editorQuad.cullFace(cullFace);
		final Direction lightFace = quad.getFace();
		editorQuad.lightFace(lightFace);
		editorQuad.nominalFace(lightFace);
		editorQuad.colorIndex(quad.getColorIndex());
		editorQuad.material(defaultMaterial);
		editorQuad.shade(quad.hasShade());

		if (!transform.transform(editorQuad)) {
			return;
		}

		cullFace = editorQuad.cullFace();

		if (cullFace != null && !blockInfo.shouldDrawFace(cullFace)) {
			return;
		}

		if (!editorQuad.material().disableAo(0)) {
			// needs to happen before offsets are applied
			editorQuad.invalidateShape();
			aoCalc.compute(editorQuad, true);
			tesselateSmooth(editorQuad, blockInfo.defaultLayer, editorQuad.colorIndex());
		} else {
			// Recomputing whether the quad has a light face is only needed if it doesn't also have a cull face,
			// as in those cases, the cull face will always be used to offset the light sampling position
			if (cullFace == null) {
				editorQuad.invalidateShape();
				// Can't rely on lazy computation in tesselateFlat() because needs to happen before offsets are applied
				editorQuad.geometryFlags();
			}

			tesselateFlat(editorQuad, blockInfo.defaultLayer, editorQuad.colorIndex());
		}
	}
}
