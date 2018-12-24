/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.client.rendercache;

import net.fabricmc.fabric.api.client.model.MultiRenderLayerBlock;
import net.fabricmc.fabric.impl.client.model.RenderCacheHelperImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.class_852;
import net.minecraft.class_853;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderDataTask;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@Mixin(ChunkRenderer.class)
public class MixinChunkRenderer {
	private static final BlockRenderLayer[] fabric_layers = BlockRenderLayer.values();

	/**
	 * This can fail softly, as all it breaks is some edge cases of rendering. Not a big loss.
	 */
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderLayer()Lnet/minecraft/client/render/block/BlockRenderLayer;", ordinal = 0), method = "method_3652", locals = LocalCapture.CAPTURE_FAILSOFT)
	public void hook(float f1, float f2, float f3, ChunkRenderDataTask task, CallbackInfo info, ChunkRenderData chunkRenderData_1, BlockPos posFrom, BlockPos posTo, class_852 class_852_1, Set set_1, class_853 worldView, boolean booleans_1[], Random random, BlockRenderManager blockRenderManager_1, Iterator var16, BlockPos.Mutable tesselatedBlockPos, BlockState blockState, Block block_1) {
		int mask = ((MultiRenderLayerBlock) block_1).getExtraRenderLayerMask();
		if (mask != 0) {
			for (BlockRenderLayer layer : fabric_layers) {
				if ((mask & (1 << layer.ordinal())) != 0) {
					RenderCacheHelperImpl.DYNAMIC_MODEL_RENDER_LAYER.set(layer);
					BufferBuilder bufferBuilder = task.getBufferBuilders().get(layer.ordinal());
					if (!chunkRenderData_1.method_3649(layer)) {
						chunkRenderData_1.method_3647(layer);
						this.method_3655(bufferBuilder, posFrom);
					}

					booleans_1[layer.ordinal()] |= blockRenderManager_1.tesselateBlock(blockState, tesselatedBlockPos, worldView, bufferBuilder, random);
				}
			}

			RenderCacheHelperImpl.DYNAMIC_MODEL_RENDER_LAYER.remove();
		}
	}

	@Shadow
	private void method_3655(BufferBuilder bufferBuilder_1, BlockPos blockPos_1) {

	}
}
