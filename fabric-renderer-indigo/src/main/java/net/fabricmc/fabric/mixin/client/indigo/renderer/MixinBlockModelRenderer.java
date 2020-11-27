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

package net.fabricmc.fabric.mixin.client.indigo.renderer;

import java.util.BitSet;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessBlockModelRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.VanillaAoHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderContext;

@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelRenderer implements AccessBlockModelRenderer {
	@Shadow
	protected abstract void getQuadDimensions(BlockRenderView blockView, BlockState blockState, BlockPos blockPos, int[] vertexData, Direction face, float[] aoData, BitSet controlBits);

	private final ThreadLocal<BlockRenderContext> CONTEXTS = ThreadLocal.withInitial(BlockRenderContext::new);

	@Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLjava/util/Random;JI)Z", cancellable = true)
	private void hookRender(BlockRenderView blockView, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, VertexConsumer buffer, boolean checkSides, Random rand, long seed, int overlay, CallbackInfoReturnable<Boolean> ci) {
		if (!((FabricBakedModel) model).isVanillaAdapter()) {
			BlockRenderContext context = CONTEXTS.get();
			// Note that we do not support face-culling here (so checkSides is ignored)
			ci.setReturnValue(context.render(blockView, model, state, pos, matrix, buffer, rand, seed, overlay));
		}
	}

	@Inject(at = @At("RETURN"), method = "<init>*")
	private void onInit(CallbackInfo ci) {
		VanillaAoHelper.initialize((BlockModelRenderer) (Object) this);
	}

	@Override
	public void fabric_updateShape(BlockRenderView blockView, BlockState blockState, BlockPos pos, int[] vertexData, Direction face, float[] aoData, BitSet controlBits) {
		getQuadDimensions(blockView, blockState, pos, vertexData, face, aoData, controlBits);
	}
}
