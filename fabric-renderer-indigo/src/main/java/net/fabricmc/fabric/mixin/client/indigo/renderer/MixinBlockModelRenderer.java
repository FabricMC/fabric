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

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderContext;

@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelRenderer {
	@Shadow
	protected BlockColors colorMap;
	private final ThreadLocal<BlockRenderContext> CONTEXTS = ThreadLocal.withInitial(BlockRenderContext::new);

	@Inject(at = @At("HEAD"), method = "tesselate", cancellable = true)
	private void hookTesselate(ExtendedBlockView blockView, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, Random rand, long seed, CallbackInfoReturnable<Boolean> ci) {
		if (!((FabricBakedModel) model).isVanillaAdapter()) {
			BlockRenderContext context = CONTEXTS.get();

			if (!context.isCallingVanilla()) {
				ci.setReturnValue(CONTEXTS.get().tesselate((BlockModelRenderer) (Object) this, blockView, model, state, pos, buffer, seed));
			}
		}
	}
}
