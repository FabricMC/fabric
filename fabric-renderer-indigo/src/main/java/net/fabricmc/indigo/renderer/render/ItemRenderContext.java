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

import static net.fabricmc.indigo.renderer.render.AbstractQuadRenderer.FULL_BRIGHTNESS;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

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
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
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
	private int color;
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

	public void renderModel(FabricBakedModel model, ItemStack stack, int color, class_4587 matrixStack, class_4588 buffer, VanillaQuadHandler vanillaHandler) {
		this.color = color;
		this.itemStack = stack;
		this.bufferBuilder = buffer;
		this.matrixStack = matrixStack;

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
			//TODO: populate
			renderQuad(null);
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
			//TODO: populate
			renderQuad(null);
		}
	};

	private int quadColor() {
		final int colorIndex = editorQuad.colorIndex();
		int quadColor = color;

		if (quadColor == -1 && colorIndex != -1) {
			quadColor = colorMap.getColorMultiplier(itemStack, colorIndex);
			quadColor |= 0xFF000000;
		}
		return quadColor;
	}

	private void colorizeAndOutput(int quadColor, Matrix4f matrix) {
		final MutableQuadViewImpl q = editorQuad;

		for (int i = 0; i < 4; i++) {
			int c = q.spriteColor(i, 0);
			c = ColorHelper.multiplyColor(quadColor, c);
			q.spriteColor(i, 0, ColorHelper.swapRedBlueIfNeeded(c));
		}

		AbstractQuadRenderer.bufferQuad((BufferBuilder) bufferBuilder, q, matrix);
	}

	private void renderQuad(Matrix4f matrix) {
		final MutableQuadViewImpl quad = editorQuad;

		if (!transform(editorQuad)) {
			return;
		}

		RenderMaterialImpl.Value mat = quad.material();
		final int quadColor = quadColor();

		quad.populateMissingNormals();
		quad.lightmap(FULL_BRIGHTNESS, FULL_BRIGHTNESS, FULL_BRIGHTNESS, FULL_BRIGHTNESS);

		colorizeAndOutput(mat.disableColorIndex(0) ? -1 : quadColor, matrix);
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
				renderFallbackWithTransform((BufferBuilder) bufferBuilder, model.getQuads((BlockState) null, cullFace, random), color, itemStack, cullFace);
			}
		} else {
			for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
				random.setSeed(ITEM_RANDOM_SEED);
				//TODO: put back
				//vanillaHandler.accept(bufferBuilder, model.getQuads((BlockState) null, ModelHelper.faceFromIndex(i), random), color, itemStack);
			}
		}
	};

	private void renderFallbackWithTransform(BufferBuilder bufferBuilder, List<BakedQuad> quads, int color, ItemStack stack, Direction cullFace) {
		if (quads.isEmpty()) {
			return;
		}

		if (CompatibilityHelper.canRender(quads.get(0).getVertexData())) {
			Maker editorQuad = this.editorQuad;

			for (BakedQuad q : quads) {
				editorQuad.clear();
				editorQuad.fromVanilla(q.getVertexData(), 0, false);
				editorQuad.cullFace(cullFace);
				final Direction lightFace = q.getFace();
				editorQuad.lightFace(lightFace);
				editorQuad.nominalFace(lightFace);
				editorQuad.colorIndex(q.getColorIndex());
				//TODO: populate
				renderQuad(null);
			}
		} else {
			//TODO: put back
//			vanillaHandler.accept(bufferBuilder, quads, color, stack);
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
