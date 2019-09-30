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

package net.fabricmc.indigo.renderer.render;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.indigo.renderer.helper.ColorHelper;
import net.fabricmc.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.indigo.renderer.mesh.MeshImpl;
import net.fabricmc.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * The render context used for item rendering. 
 * Does not implement emissive lighting for sake
 * of simplicity in the default renderer. 
 */
public class ItemRenderContext extends AbstractRenderContext implements RenderContext {
	/** Value vanilla uses for item rendering.  The only sensible choice, of course.  */
	private static final long ITEM_RANDOM_SEED = 42L;

	/** used to accept a method reference from the ItemRenderer */
	@FunctionalInterface
	public static interface VanillaQuadHandler {
		void accept(BakedModel model, ItemStack stack, int color, class_4587 matrixStack, class_4588 buffer);
	}

	private final ItemColors colorMap;
	private final Random random = new Random();
	private final Consumer<BakedModel> fallbackConsumer;
	class_4588 bufferBuilder;
	class_4587 matrixStack;
	Matrix4f matrix;
	private int lightmap;
	private ItemStack itemStack;
	private VanillaQuadHandler vanillaHandler;

	private final Supplier<Random> randomSupplier = () -> {
		Random result = random;
		result.setSeed(ITEM_RANDOM_SEED);
		return random;
	};

	private final int[] quadData = new int[EncodingFormat.TOTAL_STRIDE];;

	public ItemRenderContext(ItemColors colorMap) {
		this.colorMap = colorMap;
		this.fallbackConsumer = this::fallbackConsumer;
	}

	public void renderModel(FabricBakedModel model, ItemStack stack, int lightmap, class_4587 matrixStack, class_4588 buffer, VanillaQuadHandler vanillaHandler) {
		this.lightmap = lightmap;
		this.itemStack = stack;
		this.bufferBuilder = buffer;
		this.matrixStack = matrixStack;
		this.matrix = matrixStack.method_22910();

		this.vanillaHandler = vanillaHandler;
		model.emitItemQuads(stack, randomSupplier, this);

		this.bufferBuilder = null;
		this.matrixStack = null;
		this.itemStack = null;
		this.vanillaHandler = null;
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
		MeshImpl m = (MeshImpl) mesh;
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

		RenderMaterialImpl.Value mat = quad.material();
		final int quadColor = mat.disableColorIndex(0) ? -1 : indexColor();
		final int lightmap = mat.emissive(0) ? AbstractQuadRenderer.FULL_BRIGHTNESS : this.lightmap;

		quad.populateMissingNormals();

		for (int i = 0; i < 4; i++) {
			int c = quad.spriteColor(i, 0);
			c = ColorHelper.multiplyColor(quadColor, c);
			quad.spriteColor(i, 0, ColorHelper.swapRedBlueIfNeeded(c));
			quad.lightmap(i, ColorHelper.maxBrightness(quad.lightmap(i), lightmap));
		}

		AbstractQuadRenderer.bufferQuad(bufferBuilder, quad, matrix);
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
				renderFallbackWithTransform(bufferBuilder, model.getQuads((BlockState) null, cullFace, random), lightmap, itemStack, cullFace);
			}
		} else {
			for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
				vanillaHandler.accept(model, itemStack, lightmap, matrixStack, bufferBuilder);
			}
		}
	};

	private void renderFallbackWithTransform(class_4588 bufferBuilder, List<BakedQuad> quads, int color, ItemStack stack, Direction cullFace) {
		if (quads.isEmpty()) {
			return;
		}

		Maker editorQuad = this.editorQuad;

		for (BakedQuad q : quads) {
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
