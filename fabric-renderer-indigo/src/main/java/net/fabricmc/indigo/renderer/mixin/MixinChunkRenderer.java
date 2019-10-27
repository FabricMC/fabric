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

package net.fabricmc.indigo.renderer.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.indigo.Indigo;
import net.fabricmc.indigo.renderer.accessor.AccessChunkRenderer;
import net.fabricmc.indigo.renderer.accessor.AccessChunkRendererRegion;
import net.fabricmc.indigo.renderer.render.TerrainRenderContext;

/**
 * Implements the main hooks for terrain rendering. Attempts to tread
 * lightly. This means we are deliberately stepping over some minor
 * optimization opportunities.
 *
 * <p>Non-Fabric renderer implementations that are looking to maximize
 * performance will likely take a much more aggressive approach.
 * For that reason, mod authors who want compatibility with advanced
 * renderers will do well to steer clear of chunk rebuild hooks unless
 * they are creating a renderer.
 *
 * <p>These hooks are intended only for the Fabric default renderer and
 * aren't expected to be present when a different renderer is being used.
 * Renderer authors are responsible for creating the hooks they need.
 * (Though they can use these as a example if they wish.)
 */
@Mixin(ChunkRenderer.class)
public abstract class MixinChunkRenderer implements AccessChunkRenderer {
	@Shadow
	private BlockPos.Mutable origin;

	@Shadow
	abstract void beginBufferBuilding(BufferBuilder bufferBuilder_1, BlockPos blockPos_1);
	@Shadow
	abstract void endBufferBuilding(BlockRenderLayer blockRenderLayer_1, float float_1, float float_2, float float_3, BufferBuilder bufferBuilder_1, ChunkRenderData chunkRenderData_1);

	/**
	 * Access method for renderer.
	 */
	@Override
	public void fabric_beginBufferBuilding(BufferBuilder bufferBuilder_1, BlockPos blockPos_1) {
		beginBufferBuilding(bufferBuilder_1, blockPos_1);
	}

	/**
	 * Save task to renderer, this is the easiest place to capture it.
	 */
	@Inject(at = @At("HEAD"), method = "rebuildChunk")
	private void hookRebuildChunkHead(float float_1, float float_2, float float_3, ChunkRenderTask chunkRenderTask_1, CallbackInfo info) {
		if (chunkRenderTask_1 != null) {
			TerrainRenderContext renderer = TerrainRenderContext.POOL.get();
			renderer.setChunkTask(chunkRenderTask_1);
		}
	}

	/**
	 * Capture the block layer result flags when they are first created so our renderer
	 * can update then when more than one layer is renderer for a single model.
	 * This is also where we signal the renderer to prepare for a new chunk using
	 * the data we've accumulated up to this point.
	 */
	@ModifyVariable(method = "rebuildChunk", at = @At(value = "STORE", ordinal = 0), allow = 1, require = 1)
	private boolean[] hookResultFlagsAndPrepare(boolean[] flagsIn) {
		TerrainRenderContext.POOL.get().prepare((ChunkRenderer) (Object) this, origin, flagsIn);
		return flagsIn;
	}

	/**
	 * This is the hook that actually implements the rendering API for terrain rendering.
	 *
	 * <p>It's unusual to have a @Redirect in a Fabric library, but in this case
	 * it is our explicit intention that {@link BlockRenderManager#tesselateBlock(BlockState, BlockPos, ExtendedBlockView, BufferBuilder, Random)}
	 * does not execute for models that will be rendered by our renderer.
	 *
	 * <p>Any mod that wants to redirect this specific call is likely also a renderer, in which case this
	 * renderer should not be present, or the mod should probably instead be relying on the renderer API
	 * which was specifically created to provide for enhanced terrain rendering.
	 *
	 * <p>Note also that {@link BlockRenderManager#tesselateBlock(BlockState, BlockPos, ExtendedBlockView, BufferBuilder, Random)}
	 * IS called if the block render type is something other than {@link BlockRenderType#MODEL}.
	 * Normally this does nothing but will allow mods to create rendering hooks that are
	 * driven off of render type. (Not recommended or encouraged, but also not prevented.)
	 */
	@Redirect(method = "rebuildChunk", require = 1,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;tesselateBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/ExtendedBlockView;Lnet/minecraft/client/render/BufferBuilder;Ljava/util/Random;)Z"))
	private boolean hookChunkBuildTesselate(BlockRenderManager renderManager, BlockState blockState, BlockPos blockPos, ExtendedBlockView blockView, BufferBuilder bufferBuilder, Random random) {
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			final BakedModel model = renderManager.getModel(blockState);

			if (Indigo.ALWAYS_TESSELATE_INDIGO || !((FabricBakedModel) model).isVanillaAdapter()) {
				return ((AccessChunkRendererRegion) blockView).fabric_getRenderer().tesselateBlock(blockState, blockPos, model);
			}
		}

		return renderManager.tesselateBlock(blockState, blockPos, blockView, bufferBuilder, random);
	}

	/**
	 * Release all references. Probably not necessary but would be $#%! to debug if it is.
	 */
	@Inject(at = @At("RETURN"), method = "rebuildChunk")
	private void hookRebuildChunkReturn(float float_1, float float_2, float float_3, ChunkRenderTask chunkRenderTask_1, CallbackInfo info) {
		TerrainRenderContext.POOL.get().release();
	}
}
