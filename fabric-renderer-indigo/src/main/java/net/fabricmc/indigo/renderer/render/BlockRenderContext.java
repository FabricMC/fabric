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

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.indigo.renderer.aocalc.AoLuminanceFix;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Context for non-terrain block rendering.
 */
public class BlockRenderContext extends AbstractRenderContext implements RenderContext {
	private final BlockRenderInfo blockInfo = new BlockRenderInfo();
	private final AoCalculator aoCalc = new AoCalculator(blockInfo, this::brightness, this::aoLevel);
	private final MeshConsumer meshConsumer = new MeshConsumer(blockInfo, this::outputBuffer, aoCalc, this::transform);
	private final Random random = new Random();
	private BlockModelRenderer vanillaRenderer;
	private BufferBuilder bufferBuilder;
	private long seed;
	private boolean isCallingVanilla = false;
	private boolean didOutput = false;

	public boolean isCallingVanilla() {
		return isCallingVanilla;
	}

	private int brightness(BlockPos pos) {
		if (blockInfo.blockView == null) {
			return 15 << 20 | 15 << 4;
		}

		return blockInfo.blockView.getLightmapIndex(blockInfo.blockView.getBlockState(pos), pos);
	}

	private float aoLevel(BlockPos pos) {
		final BlockRenderView blockView = blockInfo.blockView;
		return blockView == null ? 1f : AoLuminanceFix.INSTANCE.apply(blockView, pos);
	}

	private BufferBuilder outputBuffer(BlockRenderLayer renderLayer) {
		didOutput = true;
		return bufferBuilder;
	}

	public boolean tesselate(BlockModelRenderer vanillaRenderer, BlockRenderView blockView, BakedModel model, BlockState state, BlockPos pos, class_4587 matrixStack, class_4588 buffer, boolean checkSides, long seed) {
		this.vanillaRenderer = vanillaRenderer;
		this.bufferBuilder = (BufferBuilder) buffer;
		this.prepareMatrix(state, pos, blockView, matrixStack);

		this.seed = seed;
		this.didOutput = false;
		aoCalc.clear();
		blockInfo.setBlockView(blockView);
		blockInfo.prepareForBlock(state, pos, model.useAmbientOcclusion());

		((FabricBakedModel) model).emitBlockQuads(blockView, state, pos, blockInfo.randomSupplier, this);

		this.vanillaRenderer = null;
		blockInfo.release();
		this.bufferBuilder = null;

		matrixStack.method_22909();

		return didOutput;
	}

	protected void acceptVanillaModel(BakedModel model) {
		isCallingVanilla = true;
		didOutput = didOutput && vanillaRenderer.tesselate(blockInfo.blockView, model, blockInfo.blockState, blockInfo.blockPos, matrixStack, (class_4588) bufferBuilder, false, random, seed);
		isCallingVanilla = false;
	}

	private class MeshConsumer extends AbstractMeshConsumer {
		MeshConsumer(BlockRenderInfo blockInfo, Function<BlockRenderLayer, BufferBuilder> bufferFunc, AoCalculator aoCalc, QuadTransform transform) {
			super(blockInfo, bufferFunc, aoCalc, transform);
		}

		@Override
		protected Matrix4f matrix() {
			return matrix;
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
