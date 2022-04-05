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
import java.util.function.Supplier;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;

/**
 * The render context used for item rendering.
 * Does not implement emissive lighting for sake
 * of simplicity in the default renderer.
 */
public class ItemRenderContext extends AbstractRenderContext {
	/** Value vanilla uses for item rendering.  The only sensible choice, of course.  */
	private static final long ITEM_RANDOM_SEED = 42L;

	/** used to accept a method reference from the ItemRenderer. */
	@FunctionalInterface
	public interface VanillaQuadHandler {
		void accept(BakedModel model, ItemStack stack, int color, int overlay, MatrixStack matrixStack, VertexConsumer buffer);
	}

	private final ItemColors colorMap;
	private final Random random = new Random();
	private final Vec3f normalVec = new Vec3f();

	private final Supplier<Random> randomSupplier = () -> {
		random.setSeed(ITEM_RANDOM_SEED);
		return random;
	};

	private final MeshConsumer meshConsumer = new MeshConsumer();
	private final FallbackConsumer fallbackConsumer = new FallbackConsumer();

	private MatrixStack matrixStack;
	private VertexConsumerProvider vertexConsumerProvider;
	private VertexConsumer modelVertexConsumer;
	private BlendMode quadBlendMode;
	private VertexConsumer quadVertexConsumer;
	private Mode transformMode;
	private int lightmap;
	private ItemStack itemStack;
	private VanillaQuadHandler vanillaHandler;

	public ItemRenderContext(ItemColors colorMap) {
		this.colorMap = colorMap;
	}

	public void renderModel(ItemStack itemStack, Mode transformMode, boolean invert, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightmap, int overlay, BakedModel model, VanillaQuadHandler vanillaHandler) {
		this.lightmap = lightmap;
		this.overlay = overlay;
		this.itemStack = itemStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
		this.matrixStack = matrixStack;
		this.transformMode = transformMode;
		this.vanillaHandler = vanillaHandler;
		quadBlendMode = BlendMode.DEFAULT;
		modelVertexConsumer = selectVertexConsumer(RenderLayers.getItemLayer(itemStack, transformMode != ModelTransformation.Mode.GROUND));

		matrixStack.push();
		model.getTransformation().getTransformation(transformMode).apply(invert, matrixStack);
		matrixStack.translate(-0.5D, -0.5D, -0.5D);
		matrix = matrixStack.peek().getPositionMatrix();
		normalMatrix = matrixStack.peek().getNormalMatrix();

		((FabricBakedModel) model).emitItemQuads(itemStack, randomSupplier, this);

		matrixStack.pop();

		this.matrixStack = null;
		this.itemStack = null;
		this.vanillaHandler = null;
		modelVertexConsumer = null;
	}

	private class Maker extends MutableQuadViewImpl implements QuadEmitter {
		{
			data = new int[EncodingFormat.TOTAL_STRIDE];
			clear();
		}

		@Override
		public Maker emit() {
			computeGeometry();
			renderMeshQuad(this);
			clear();
			return this;
		}
	}

	private final Maker editorQuad = new Maker();

	/**
	 * Use non-culling translucent material in GUI to match vanilla behavior. If the item
	 * is enchanted then also select a dual-output vertex consumer. For models with layered
	 * coplanar polygons this means we will render the glint more than once. Indigo doesn't
	 * support sprite layers, so this can't be helped in this implementation.
	 */
	private VertexConsumer selectVertexConsumer(RenderLayer layerIn) {
		final RenderLayer layer = transformMode == ModelTransformation.Mode.GUI ? TexturedRenderLayers.getEntityTranslucentCull() : layerIn;
		return ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, layer, true, itemStack.hasGlint());
	}

	/**
	 * Caches custom blend mode / vertex consumers and mimics the logic
	 * in {@code RenderLayers.getEntityBlockLayer}. Layers other than
	 * translucent are mapped to cutout.
	 */
	private VertexConsumer quadVertexConsumer(BlendMode blendMode) {
		if (blendMode == BlendMode.DEFAULT) {
			return modelVertexConsumer;
		}

		if (blendMode != BlendMode.TRANSLUCENT) {
			blendMode = BlendMode.CUTOUT;
		}

		if (blendMode == quadBlendMode) {
			return quadVertexConsumer;
		} else if (blendMode == BlendMode.TRANSLUCENT) {
			quadVertexConsumer = selectVertexConsumer(TexturedRenderLayers.getEntityTranslucentCull());
			quadBlendMode = BlendMode.TRANSLUCENT;
		} else {
			quadVertexConsumer = selectVertexConsumer(TexturedRenderLayers.getEntityCutout());
			quadBlendMode = BlendMode.CUTOUT;
		}

		return quadVertexConsumer;
	}

	private void bufferQuad(MutableQuadViewImpl quad, BlendMode blendMode) {
		AbstractQuadRenderer.bufferQuad(quadVertexConsumer(blendMode), quad, matrix, overlay, normalMatrix, normalVec);
	}

	private void colorizeQuad(MutableQuadViewImpl q, int colorIndex) {
		if (colorIndex == -1) {
			for (int i = 0; i < 4; i++) {
				q.spriteColor(i, 0, ColorHelper.swapRedBlueIfNeeded(q.spriteColor(i, 0)));
			}
		} else {
			final int itemColor = 0xFF000000 | colorMap.getColor(itemStack, colorIndex);

			for (int i = 0; i < 4; i++) {
				q.spriteColor(i, 0, ColorHelper.swapRedBlueIfNeeded(ColorHelper.multiplyColor(itemColor, q.spriteColor(i, 0))));
			}
		}
	}

	private void renderQuad(MutableQuadViewImpl quad, BlendMode blendMode, int colorIndex) {
		colorizeQuad(quad, colorIndex);

		final int lightmap = this.lightmap;

		for (int i = 0; i < 4; i++) {
			quad.lightmap(i, ColorHelper.maxBrightness(quad.lightmap(i), lightmap));
		}

		bufferQuad(quad, blendMode);
	}

	private void renderQuadEmissive(MutableQuadViewImpl quad, BlendMode blendMode, int colorIndex) {
		colorizeQuad(quad, colorIndex);

		for (int i = 0; i < 4; i++) {
			quad.lightmap(i, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		}

		bufferQuad(quad, blendMode);
	}

	private void renderMeshQuad(MutableQuadViewImpl quad) {
		if (!transform(quad)) {
			return;
		}

		final RenderMaterialImpl.Value mat = quad.material();

		final int colorIndex = mat.disableColorIndex(0) ? -1 : quad.colorIndex();
		final BlendMode blendMode = mat.blendMode(0);

		if (mat.emissive(0)) {
			renderQuadEmissive(quad, blendMode, colorIndex);
		} else {
			renderQuad(quad, blendMode, colorIndex);
		}
	}

	private class MeshConsumer implements Consumer<Mesh> {
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
				renderMeshQuad(editorQuad);
			}
		}
	}

	private class FallbackConsumer implements Consumer<BakedModel> {
		@Override
		public void accept(BakedModel model) {
			if (hasTransform()) {
				// if there's a transform in effect, convert to mesh-based quads so that we can apply it
				for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
					final Direction cullFace = ModelHelper.faceFromIndex(i);
					random.setSeed(ITEM_RANDOM_SEED);
					final List<BakedQuad> quads = model.getQuads(null, cullFace, random);
					final int count = quads.size();

					if (count != 0) {
						for (int j = 0; j < count; j++) {
							final BakedQuad q = quads.get(j);
							renderQuadWithTransform(q, cullFace);
						}
					}
				}
			} else {
				vanillaHandler.accept(model, itemStack, lightmap, overlay, matrixStack, modelVertexConsumer);
			}
		}

		private void renderQuadWithTransform(BakedQuad quad, Direction cullFace) {
			final Maker editorQuad = ItemRenderContext.this.editorQuad;
			editorQuad.fromVanilla(quad, IndigoRenderer.MATERIAL_STANDARD, cullFace);

			if (!transform(editorQuad)) {
				return;
			}

			renderQuad(editorQuad, BlendMode.DEFAULT, editorQuad.colorIndex());
		}
	}

	@Override
	public Consumer<Mesh> meshConsumer() {
		return meshConsumer;
	}

	@Override
	public Consumer<BakedModel> fallbackConsumer() {
		return fallbackConsumer;
	}

	@Override
	public QuadEmitter getEmitter() {
		editorQuad.clear();
		return editorQuad;
	}
}
