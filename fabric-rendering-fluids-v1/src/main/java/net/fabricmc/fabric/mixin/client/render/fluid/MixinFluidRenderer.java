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

package net.fabricmc.fabric.mixin.client.render.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.render.fluid.FluidRendererHookContainer;
import net.fabricmc.fabric.impl.client.render.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(at = @At("HEAD"), method = "tesselate", cancellable = true)
    public void tesselate(ExtendedBlockView view, BlockPos pos, BufferBuilder bufferBuilder, FluidState state, CallbackInfoReturnable<Boolean> info) {
        FluidRendererHookContainer ctr = fabric_renderHandler.get();
        FluidRenderHandler handler = FluidRenderHandlerRegistryImpl.INSTANCE.getOverride(state.getFluid());
        if (handler == null) {
            return;
        }

        /* ActionResult hResult = handler.tesselate(view, pos, bufferBuilder, state);
        if (hResult != ActionResult.PASS) {
            info.setReturnValue(hResult == ActionResult.SUCCESS);
            return;
        } */

        ctr.view = view;
        ctr.pos = pos;
        ctr.state = state;
        ctr.handler = handler;
    }

    @Inject(at = @At("RETURN"), method = "tesselate")
    public void tesselateReturn(ExtendedBlockView view, BlockPos pos, BufferBuilder bufferBuilder, FluidState state, CallbackInfoReturnable<Boolean> info) {
        fabric_renderHandler.get().clear();
    }

    @ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/client/render/block/FluidRenderer.isSameFluid(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)Z"), method = "tesselate", ordinal = 0)
    public boolean modLavaCheck(boolean chk) {
        return fabric_renderHandler.get().handler != null || chk;
    }

    @ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/client/render/block/FluidRenderer.isSameFluid(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)Z"), method = "tesselate", ordinal = 0)
    public Sprite[] modSpriteArray(Sprite[] chk) {
        FluidRendererHookContainer ctr = fabric_renderHandler.get();
        return ctr.handler != null ? ctr.handler.getFluidSprites(ctr.view, ctr.pos, ctr.state) : chk;
    }

    @ModifyVariable(at = @At(value = "CONSTANT", args = "intValue=16", ordinal = 0, shift = At.Shift.BEFORE), method = "tesselate", ordinal = 0)
    public int modTintColor(int chk) {
        FluidRendererHookContainer ctr = fabric_renderHandler.get();
        return ctr.handler != null ? ctr.handler.getFluidColor(ctr.view, ctr.pos, ctr.state) : chk;
    }
}
