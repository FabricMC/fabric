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

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoLuminanceFix;

/**
 * Context for non-terrain block rendering.
 */
public class BlockRenderContext extends AbstractRenderContext implements RenderContext {
	private final BlockRenderInfo blockInfo = new BlockRenderInfo();
	private final AoCalculator aoCalc = new AoCalculator(blockInfo, this::brightness, this::aoLevel);
	private final MeshConsumer meshConsumer = new MeshConsumer(blockInfo, this::outputBuffer, aoCalc, this::transform);
	private final Random random = new Random();
	private BlockModelRenderer vanillaRenderer;
	private VertexConsumer bufferBuilder;
	private long seed;
	private boolean isCallingVanilla = false;
	private boolean didOutput = false;
	private MatrixStack matrixStack;

	public boolean isCallingVanilla() {
		return isCallingVanilla;
	}

	private int brightness(BlockPos pos) {
		if (blockInfo.blockView == null) {
			return 15 << 20 | 15 << 4;
		}

		return WorldRenderer.getLightmapCoordinates(blockInfo.blockView, blockInfo.blockView.getBlockState(pos), pos);
	}

	private float aoLevel(BlockPos pos) {
		final BlockRenderView blockView = blockInfo.blockView;
		return blockView == null ? 1f : AoLuminanceFix.INSTANCE.apply(blockView, pos);
	}

	private VertexConsumer outputBuffer(RenderLayer renderLayer) {
		didOutput = true;
		return bufferBuilder;
	}

	public boolean tesselate(BlockModelRenderer vanillaRenderer, BlockRenderView blockView, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrixStack, VertexConsumer buffer, boolean checkSides, long seed, int overlay) {
		this.vanillaRenderer = vanillaRenderer;
		this.bufferBuilder = buffer;
		this.matrixStack = matrixStack;
		this.matrix = matrixStack.peek().getModel();
		this.normalMatrix = matrixStack.peek().getNormal();

		this.seed = seed;
		this.overlay = overlay;
		this.didOutput = false;
		aoCalc.clear();
		blockInfo.setBlockView(blockView);
		blockInfo.prepareForBlock(state, pos, model.useAmbientOcclusion());

		((FabricBakedModel) model).emitBlockQuads(blockView, state, pos, blockInfo.randomSupplier, this);

		this.vanillaRenderer = null;
		blockInfo.release();
		this.bufferBuilder = null;

		return didOutput;
	}

	protected void acceptVanillaModel(BakedModel model) {
		isCallingVanilla = true;
		didOutput = didOutput && vanillaRenderer.render(blockInfo.blockView, model, blockInfo.blockState, blockInfo.blockPos, matrixStack, bufferBuilder, false, random, seed, overlay);
		isCallingVanilla = false;
	}

	private class MeshConsumer extends AbstractMeshConsumer {
		MeshConsumer(BlockRenderInfo blockInfo, Function<RenderLayer, VertexConsumer> bufferFunc, AoCalculator aoCalc, QuadTransform transform) {
			super(blockInfo, bufferFunc, aoCalc, transform);
		}

		@Override
		protected Matrix4f matrix() {
			return matrix;
		}

		@Override
		protected Matrix3f normalMatrix() {
			return normalMatrix;
		}

		@Override
		protected int overlay() {
			return overlay;
		}
	}

	@Override
	public Consumer<Mesh> meshConsumer() {
		return meshConsumer;
	}

	@Override
	public Consumer<BakedModel> fallbackConsumer() {
		return this::acceptVanillaModel;
	}

	@Override
	public QuadEmitter getEmitter() {
		return meshConsumer.getEmitter();
	}
}
