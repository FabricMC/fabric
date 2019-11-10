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

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl.Value;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessBufferBuilder;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;

/**
 * Consumer for pre-baked meshes.  Works by copying the mesh data to a
 * "editor" quad held in the instance, where all transformations are applied before buffering.
 */
public abstract class AbstractMeshConsumer extends AbstractQuadRenderer implements Consumer<Mesh> {
	protected AbstractMeshConsumer(BlockRenderInfo blockInfo, Int2ObjectFunction<AccessBufferBuilder> bufferFunc, AoCalculator aoCalc, QuadTransform transform) {
		super(blockInfo, bufferFunc, aoCalc, transform);
	}

	/**
	 * Where we handle all pre-buffer coloring, lighting, transformation, etc.
	 * Reused for all mesh quads. Fixed baking array sized to hold largest possible mesh quad.
	 */
	private class Maker extends MutableQuadViewImpl implements QuadEmitter {
		{
			data = new int[EncodingFormat.MAX_STRIDE];
			material = (Value) IndigoRenderer.INSTANCE.materialFinder().spriteDepth(RenderMaterialImpl.MAX_SPRITE_DEPTH).find();
		}

		// only used via RenderContext.getEmitter()
		@Override
		public Maker emit() {
			lightFace = GeometryHelper.lightFace(this);
			ColorHelper.applyDiffuseShading(this, false);
			renderQuad(this);
			clear();
			return this;
		}
	}

	private final Maker editorQuad = new Maker();

	@Override
	public void accept(Mesh mesh) {
		MeshImpl m = (MeshImpl) mesh;
		final int[] data = m.data();
		final int limit = data.length;
		int index = 0;

		while (index < limit) {
			RenderMaterialImpl.Value mat = RenderMaterialImpl.byIndex(data[index]);
			final int stride = EncodingFormat.stride(mat.spriteDepth());
			System.arraycopy(data, index, editorQuad.data(), 0, stride);
			editorQuad.load();
			index += stride;
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
		final int textureCount = mat.spriteDepth();

		if (mat.hasAo && MinecraftClient.isAmbientOcclusionEnabled()) {
			// needs to happen before offsets are applied
			aoCalc.compute(q, false);
		}

		applyOffsets(q);

		// if maybe mix of emissive / non-emissive layers then
		// need to save lightmaps in case they are overwritten by emissive
		if (mat.hasEmissive && textureCount > 1) {
			captureLightmaps(q);
		}

		tesselateQuad(q, mat, 0);

		for (int t = 1; t < textureCount; t++) {
			if (!mat.emissive(t)) {
				restoreLightmaps(q);
			}

			for (int i = 0; i < 4; i++) {
				q.spriteColor(i, 0, q.spriteColor(i, t));
				q.sprite(i, 0, q.spriteU(i, t), q.spriteV(i, t));
			}

			tesselateQuad(q, mat, t);
		}
	}

	protected abstract void applyOffsets(MutableQuadViewImpl quad);

	/**
	 * Determines color index and render layer, then routes to appropriate
	 * tesselate routine based on material properties.
	 */
	private void tesselateQuad(MutableQuadViewImpl quad, RenderMaterialImpl.Value mat, int textureIndex) {
		final int colorIndex = mat.disableColorIndex(textureIndex) ? -1 : quad.colorIndex();
		final int renderLayer = blockInfo.layerIndexOrDefault(mat.blendMode(textureIndex));

		if (blockInfo.defaultAo && !mat.disableAo(textureIndex)) {
			if (mat.emissive(textureIndex)) {
				tesselateSmoothEmissive(quad, renderLayer, colorIndex);
			} else {
				tesselateSmooth(quad, renderLayer, colorIndex);
			}
		} else {
			if (mat.emissive(textureIndex)) {
				tesselateFlatEmissive(quad, renderLayer, colorIndex, lightmaps);
			} else {
				tesselateFlat(quad, renderLayer, colorIndex);
			}
		}
	}
}
