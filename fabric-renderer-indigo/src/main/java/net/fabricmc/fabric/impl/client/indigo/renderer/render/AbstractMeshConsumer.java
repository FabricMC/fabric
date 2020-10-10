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

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;

/**
 * Consumer for pre-baked meshes.  Works by copying the mesh data to a
 * "editor" quad held in the instance, where all transformations are applied before buffering.
 */
public abstract class AbstractMeshConsumer extends AbstractQuadRenderer implements Consumer<Mesh> {
	protected AbstractMeshConsumer(BlockRenderInfo blockInfo, Function<RenderLayer, VertexConsumer> bufferFunc, AoCalculator aoCalc, QuadTransform transform) {
		super(blockInfo, bufferFunc, aoCalc, transform);
	}

	/**
	 * Where we handle all pre-buffer coloring, lighting, transformation, etc.
	 * Reused for all mesh quads. Fixed baking array sized to hold largest possible mesh quad.
	 */
	private class Maker extends MutableQuadViewImpl implements QuadEmitter {
		{
			data = new int[EncodingFormat.TOTAL_STRIDE];
			material(IndigoRenderer.MATERIAL_STANDARD);
		}

		// only used via RenderContext.getEmitter()
		@Override
		public Maker emit() {
			computeGeometry();
			renderQuad(this);
			clear();
			return this;
		}
	}

	private final Maker editorQuad = new Maker();

	@Override
	public void accept(Mesh mesh) {
		final MeshImpl m = (MeshImpl) mesh;
		final int[] data = m.data();
		final int limit = data.length;
		int index = 0;

		while (index < limit) {
			System.arraycopy(data, index, editorQuad.data(), 0, EncodingFormat.TOTAL_STRIDE);
			editorQuad.load();
			index += EncodingFormat.TOTAL_STRIDE;
			renderQuad(editorQuad);
		}
	}

	public QuadEmitter getEmitter() {
		editorQuad.clear();
		return editorQuad;
	}

	private void renderQuad(MutableQuadViewImpl q) {
		if (!transform.transform(editorQuad)) {
			return;
		}

		if (!blockInfo.shouldDrawFace(q.cullFace())) {
			return;
		}

		final RenderMaterialImpl.Value mat = q.material();

		if (!mat.disableAo(0) && MinecraftClient.isAmbientOcclusionEnabled()) {
			// needs to happen before offsets are applied
			aoCalc.compute(q, false);
		}

		tesselateQuad(q, mat, 0);
	}

	/**
	 * Determines color index and render layer, then routes to appropriate
	 * tesselate routine based on material properties.
	 */
	private void tesselateQuad(MutableQuadViewImpl quad, RenderMaterialImpl.Value mat, int textureIndex) {
		final int colorIndex = mat.disableColorIndex(textureIndex) ? -1 : quad.colorIndex();
		final RenderLayer renderLayer = blockInfo.effectiveRenderLayer(mat.blendMode(textureIndex));

		if (blockInfo.defaultAo && !mat.disableAo(textureIndex)) {
			if (mat.emissive(textureIndex)) {
				tesselateSmoothEmissive(quad, renderLayer, colorIndex);
			} else {
				tesselateSmooth(quad, renderLayer, colorIndex);
			}
		} else {
			if (mat.emissive(textureIndex)) {
				tesselateFlatEmissive(quad, renderLayer, colorIndex);
			} else {
				tesselateFlat(quad, renderLayer, colorIndex);
			}
		}
	}
}
