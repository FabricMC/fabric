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

package net.fabricmc.fabric.mixin.fluid.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;
import net.fabricmc.fabric.impl.fluid.FabricFluidCamera;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Shadow private static float red;
    @Shadow private static float green;
    @Shadow private static float blue;
    @Shadow private static long lastWaterFogColorUpdateTime = -1L;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void render(Camera camera, float tickDelta, ClientWorld world, int i, float f, CallbackInfo ci) {
        FluidState fluidState = ((FabricFluidCamera)camera).getSubmergedFluidState();
        if (fluidState.getFluid() instanceof FabricFlowableFluid fluid) {
			//Gets the fog color from the fluid that submerges the camera
            int fogColor = fluid.getFogColor(camera.getFocusedEntity(), tickDelta, world);
			if (fogColor != -1) {
				red = (fogColor >> 16 & 255) / 255f;
				green = (fogColor >> 8 & 255) / 255f;
				blue = (fogColor & 255) / 255f;
				lastWaterFogColorUpdateTime = -1L;

				//Apply the fog color if the current entity is submerged by an extended fluid
				RenderSystem.clearColor(red, green, blue, 0.0f);

				ci.cancel();
			}
        }
    }

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        FluidState fluidState = ((FabricFluidCamera)camera).getSubmergedFluidState();
        if (fluidState.getFluid() instanceof FabricFlowableFluid fluid) {
			Entity entity = camera.getFocusedEntity();

			//Apply the fog start and fog end, after getting them from the fluid that submerges the camera
            RenderSystem.setShaderFogStart(fluid.getFogStart(entity, fogType, viewDistance, thickFog));
            RenderSystem.setShaderFogEnd(fluid.getFogEnd(entity, fogType, viewDistance, thickFog));

            ci.cancel();
        }
    }
}
