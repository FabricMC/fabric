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
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.client.color.item.ItemColors;
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
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;

/**
 * The render context used for item rendering.
 * Does not implement emissive lighting for sake
 * of simplicity in the default renderer.
 */
public class ItemRenderContext extends AbstractRenderContext implements RenderContext {
	/** Value vanilla uses for item rendering.  The only sensible choice, of course.  */
	private static final long ITEM_RANDOM_SEED = 42L;

	/** used to accept a method reference from the ItemRenderer. */
	@FunctionalInterface
	public interface VanillaQuadHandler {
		void accept(BakedModel model, ItemStack stack, int color, int overlay, MatrixStack matrixStack, VertexConsumer buffer);
	}

	private final ItemColors colorMap;
	private final Random random = new Random();
	private final Consumer<BakedModel> fallbackConsumer;
	private final Vector3f normalVec = new Vector3f();

	private MatrixStack matrixStack;
	private Matrix4f matrix;
	private VertexConsumerProvider vertexConsumerProvider;
	private VertexConsumer modelVertexConsumer;
	private BlendMode quadBlendMode;
	private VertexConsumer quadVertexConsumer;
	private Mode transformMode;
	private int lightmap;
	private int overlay;
	private ItemStack itemStack;
	private VanillaQuadHandler vanillaHandler;

	private final Supplier<Random> randomSupplier = () -> {
		final Random result = random;
		result.setSeed(ITEM_RANDOM_SEED);
		return random;
	};

	private final int[] quadData = new int[EncodingFormat.TOTAL_STRIDE];

	public ItemRenderContext(ItemColors colorMap) {
		this.colorMap = colorMap;
		fallbackConsumer = this::fallbackConsumer;
	}

	public void renderModel(ItemStack itemStack, Mode transformMode, boolean invert, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightmap, int overlay, FabricBakedModel model, VanillaQuadHandler vanillaHandler) {
		this.lightmap = lightmap;
		this.overlay = overlay;
		this.itemStack = itemStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
		this.matrixStack = matrixStack;
		this.transformMode = transformMode;
		this.vanillaHandler = vanillaHandler;
		quadBlendMode = BlendMode.DEFAULT;
		modelVertexConsumer = selectVertexConsumer(RenderLayers.getItemLayer(itemStack));

		matrixStack.push();
		((BakedModel) model).getTransformation().getTransformation(transformMode).apply(invert, matrixStack);
		matrixStack.translate(-0.5D, -0.5D, -0.5D);
		matrix = matrixStack.peek().getModel();
		normalMatrix = matrixStack.peek().getNormal();

		model.emitItemQuads(itemStack, randomSupplier, this);

		matrixStack.pop();

		this.matrixStack = null;
		this.itemStack = null;
		this.vanillaHandler = null;
		modelVertexConsumer = null;
	}

	/**
	 * Use non-culling translucent material in GUI to match vanilla behavior. If the item
	 * is enchanted then also select a dual-output vertex consumer. For models with layered
	 * coplanar polygons this means we will render the glint more than once. Indigo doesn't
	 * support sprite layers, so this can't be helped in this implementation.
	 */
	private VertexConsumer selectVertexConsumer(RenderLayer layerIn) {
		final RenderLayer layer = transformMode == ModelTransformation.Mode.GUI && Objects.equals(layerIn, TexturedRenderLayers.getEntityTranslucent()) ? TexturedRenderLayers.getEntityTranslucentCull() : layerIn;
		return ItemRenderer.getArmorVertexConsumer(vertexConsumerProvider, layer, true, itemStack.hasEnchantmentGlint());
	}

	private class Maker extends MutableQuadViewImpl implements QuadEmitter {
		{
			data = quadData;
			clear();
		}

		@Override
		public Maker emit() {
			lightFace(GeometryHelper.lightFace(this));
			ColorHelper.applyDiffuseShading(this, false);
			renderQuad();
			clear();
			return this;
		}
	}

	private final Maker editorQuad = new Maker();

	private final Consumer<Mesh> meshConsumer = (mesh) -> {
		final MeshImpl m = (MeshImpl) mesh;
		final int[] data = m.data();
		final int limit = data.length;
		int index = 0;

		while (index < limit) {
			System.arraycopy(data, index, editorQuad.data(), 0, EncodingFormat.TOTAL_STRIDE);
			editorQuad.load();
			index += EncodingFormat.TOTAL_STRIDE;
			renderQuad();
		}
	};

	private int indexColor() {
		final int colorIndex = editorQuad.colorIndex();
		return colorIndex == -1 ? -1 : (colorMap.getColorMultiplier(itemStack, colorIndex) | 0xFF000000);
	}

	private void renderQuad() {
		final MutableQuadViewImpl quad = editorQuad;

		if (!transform(editorQuad)) {
			return;
		}

		final RenderMaterialImpl.Value mat = quad.material();
		final int quadColor = mat.disableColorIndex(0) ? -1 : indexColor();
		final int lightmap = mat.emissive(0) ? AbstractQuadRenderer.FULL_BRIGHTNESS : this.lightmap;

		for (int i = 0; i < 4; i++) {
			int c = quad.spriteColor(i, 0);
			c = ColorHelper.multiplyColor(quadColor, c);
			quad.spriteColor(i, 0, ColorHelper.swapRedBlueIfNeeded(c));
			quad.lightmap(i, ColorHelper.maxBrightness(quad.lightmap(i), lightmap));
		}

		AbstractQuadRenderer.bufferQuad(quadVertexConsumer(mat.blendMode(0)), quad, matrix, overlay, normalMatrix, normalVec);
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
			quadVertexConsumer = selectVertexConsumer(TexturedRenderLayers.getEntityTranslucent());
			quadBlendMode = BlendMode.TRANSLUCENT;
		} else {
			quadVertexConsumer = selectVertexConsumer(TexturedRenderLayers.getEntityCutout());
			quadBlendMode = BlendMode.CUTOUT;
		}

		return quadVertexConsumer;
	}

	@Override
	public Consumer<Mesh> meshConsumer() {
		return meshConsumer;
	}

	private void fallbackConsumer(BakedModel model) {
		if (hasTransform()) {
			// if there's a transform in effect, convert to mesh-based quads so that we can apply it
			for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
				random.setSeed(ITEM_RANDOM_SEED);
				final Direction cullFace = ModelHelper.faceFromIndex(i);
				renderFallbackWithTransform(model.getQuads((BlockState) null, cullFace, random), cullFace);
			}
		} else {
			for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
				vanillaHandler.accept(model, itemStack, lightmap, overlay, matrixStack, modelVertexConsumer);
			}
		}
	}

	private void renderFallbackWithTransform(List<BakedQuad> quads, Direction cullFace) {
		if (quads.isEmpty()) {
			return;
		}

		final Maker editorQuad = this.editorQuad;

		for (final BakedQuad q : quads) {
			editorQuad.clear();
			editorQuad.fromVanilla(q.getVertexData(), 0, false);
			editorQuad.cullFace(cullFace);
			final Direction lightFace = q.getFace();
			editorQuad.lightFace(lightFace);
			editorQuad.nominalFace(lightFace);
			editorQuad.colorIndex(q.getColorIndex());
			renderQuad();
		}
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
