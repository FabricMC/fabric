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

package net.fabricmc.fabric.mixin.client.rendering.fluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRendererHookContainer;

@Mixin(FluidRenderer.class)
public class MixinFluidRenderer {
	@Shadow
	private Sprite[] lavaSprites;
	@Shadow
	private Sprite[] waterSprites;

	private final ThreadLocal<FluidRendererHookContainer> fabric_renderHandler = ThreadLocal.withInitial(FluidRendererHookContainer::new);

	@Inject(at = @At("RETURN"), method = "onResourceReload")
	public void onResourceReloadReturn(CallbackInfo info) {
		FluidRenderHandlerRegistryImpl.INSTANCE.onFluidRendererReload(waterSprites, lavaSprites);
	}

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	public void tesselate(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> info) {
		FluidRendererHookContainer ctr = fabric_renderHandler.get();
		FluidRenderHandler handler = FluidRenderHandlerRegistryImpl.INSTANCE.getOverride(state.getFluid());

		ctr.view = view;
		ctr.pos = pos;
		ctr.state = state;
		ctr.handler = handler;

		/* if (handler == null) {
			return;
		}

		ActionResult hResult = handler.tesselate(view, pos, bufferBuilder, state);

		if (hResult != ActionResult.PASS) {
			info.setReturnValue(hResult == ActionResult.SUCCESS);
			return;
		} */
	}

	@Inject(at = @At("RETURN"), method = "render")
	public void tesselateReturn(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> info) {
		fabric_renderHandler.get().clear();
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/client/render/block/FluidRenderer.isSameFluid(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)Z"), method = "render", ordinal = 0)
	public boolean modLavaCheck(boolean chk) {
		// First boolean local is set by vanilla according to 'matches lava'
		// but uses the negation consistent with 'matches water'
		// for determining if special water sprite should be used behind glass.

		// Has other uses but those have already happened by the time the hook is called.
		final FluidRendererHookContainer ctr = fabric_renderHandler.get();
		return chk || !ctr.state.isIn(FluidTags.WATER);
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/client/render/block/FluidRenderer.isSameFluid(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)Z"), method = "render", ordinal = 0)
	public Sprite[] modSpriteArray(Sprite[] chk) {
		FluidRendererHookContainer ctr = fabric_renderHandler.get();
		return ctr.handler != null ? ctr.handler.getFluidSprites(ctr.view, ctr.pos, ctr.state) : chk;
	}

	@ModifyVariable(at = @At(value = "CONSTANT", args = "intValue=16", ordinal = 0, shift = At.Shift.BEFORE), method = "render", ordinal = 0)
	public int modTintColor(int chk) {
		FluidRendererHookContainer ctr = fabric_renderHandler.get();
		return ctr.handler != null ? ctr.handler.getFluidColor(ctr.view, ctr.pos, ctr.state) : chk;
	}
}
