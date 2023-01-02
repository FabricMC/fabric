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

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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

	private final Maker editorQuad = new Maker();
	private final MeshConsumer meshConsumer = new MeshConsumer();
	private final FallbackConsumer fallbackConsumer = new FallbackConsumer();

	private ItemStack itemStack;
	private Mode transformMode;
	private MatrixStack matrixStack;
	private VertexConsumerProvider vertexConsumerProvider;
	private int lightmap;
	private VanillaQuadHandler vanillaHandler;

	private boolean isDefaultTranslucent;
	private boolean isTranslucentDirect;
	private VertexConsumer translucentVertexConsumer;
	private VertexConsumer cutoutVertexConsumer;
	private VertexConsumer modelVertexConsumer;

	public ItemRenderContext(ItemColors colorMap) {
		this.colorMap = colorMap;
	}

	public void renderModel(ItemStack itemStack, Mode transformMode, boolean invert, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightmap, int overlay, BakedModel model, VanillaQuadHandler vanillaHandler) {
		this.itemStack = itemStack;
		this.transformMode = transformMode;
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
		this.lightmap = lightmap;
		this.overlay = overlay;
		this.vanillaHandler = vanillaHandler;
		computeOutputInfo();

		matrix = matrixStack.peek().getPositionMatrix();
		normalMatrix = matrixStack.peek().getNormalMatrix();

		((FabricBakedModel) model).emitItemQuads(itemStack, randomSupplier, this);

		this.itemStack = null;
		this.matrixStack = null;
		this.vanillaHandler = null;
		translucentVertexConsumer = null;
		cutoutVertexConsumer = null;
		modelVertexConsumer = null;
	}

	private void computeOutputInfo() {
		isDefaultTranslucent = true;
		isTranslucentDirect = true;

		Item item = itemStack.getItem();

		if (item instanceof BlockItem blockItem) {
			BlockState state = blockItem.getBlock().getDefaultState();
			RenderLayer renderLayer = RenderLayers.getBlockLayer(state);

			if (renderLayer != RenderLayer.getTranslucent()) {
				isDefaultTranslucent = false;
			}

			if (transformMode != Mode.GUI && !transformMode.isFirstPerson()) {
				isTranslucentDirect = false;
			}
		}

		modelVertexConsumer = quadVertexConsumer(BlendMode.DEFAULT);
	}

	/**
	 * Caches custom blend mode / vertex consumers and mimics the logic
	 * in {@code RenderLayers.getEntityBlockLayer}. Layers other than
	 * translucent are mapped to cutout.
	 */
	private VertexConsumer quadVertexConsumer(BlendMode blendMode) {
		boolean translucent;

		if (blendMode == BlendMode.DEFAULT) {
			translucent = isDefaultTranslucent;
		} else {
			translucent = blendMode == BlendMode.TRANSLUCENT;
		}

		if (translucent) {
			if (translucentVertexConsumer == null) {
				if (isTranslucentDirect) {
					translucentVertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, TexturedRenderLayers.getEntityTranslucentCull(), true, itemStack.hasGlint());
				} else if (MinecraftClient.isFabulousGraphicsOrBetter()) {
					translucentVertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, TexturedRenderLayers.getItemEntityTranslucentCull(), true, itemStack.hasGlint());
				} else {
					translucentVertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, TexturedRenderLayers.getEntityTranslucentCull(), true, itemStack.hasGlint());
				}
			}

			return translucentVertexConsumer;
		} else {
			if (cutoutVertexConsumer == null) {
				cutoutVertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, TexturedRenderLayers.getEntityCutout(), true, itemStack.hasGlint());
			}

			return cutoutVertexConsumer;
		}
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

	private class FallbackConsumer implements BakedModelConsumer {
		@Override
		public void accept(BakedModel model) {
			accept(model, null);
		}

		@Override
		public void accept(BakedModel model, @Nullable BlockState state) {
			if (hasTransform()) {
				// if there's a transform in effect, convert to mesh-based quads so that we can apply it
				for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
					final Direction cullFace = ModelHelper.faceFromIndex(i);
					random.setSeed(ITEM_RANDOM_SEED);
					final List<BakedQuad> quads = model.getQuads(state, cullFace, random);
					final int count = quads.size();

					if (count != 0) {
						for (int j = 0; j < count; j++) {
							final BakedQuad q = quads.get(j);
							editorQuad.fromVanilla(q, IndigoRenderer.MATERIAL_STANDARD, cullFace);
							renderMeshQuad(editorQuad);
						}
					}
				}
			} else {
				vanillaHandler.accept(model, itemStack, lightmap, overlay, matrixStack, modelVertexConsumer);
			}
		}
	}

	@Override
	public Consumer<Mesh> meshConsumer() {
		return meshConsumer;
	}

	@Override
	public BakedModelConsumer bakedModelConsumer() {
		return fallbackConsumer;
	}

	@Override
	public QuadEmitter getEmitter() {
		editorQuad.clear();
		return editorQuad;
	}
}
