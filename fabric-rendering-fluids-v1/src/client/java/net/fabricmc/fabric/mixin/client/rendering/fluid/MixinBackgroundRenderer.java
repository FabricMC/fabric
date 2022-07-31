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

import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidFogHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FogParameters;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
	@Shadow
	private static float red;

	@Shadow
	private static float green;

	@Shadow
	private static float blue;

	@Shadow
	private static long lastWaterFogColorUpdateTime;

	@Unique
	private static int fogColor = -1;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
		FluidState submergedFluidState = getSubmergedFluidState(camera);
		FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(submergedFluidState.getFluid());

		if (handler != null) {
			FluidFogHandler fogHandler = handler.getFogHandler(camera.getBlockPos(), submergedFluidState);

			if (fogHandler != null) {
				//Gets the fog color of the fluid that submerges the camera.
				fogColor = fogHandler.getFogColor(camera, tickDelta, world, viewDistance, skyDarkness);

				if (fogColor != -1) {
					//Gets the red, green, and blue values.
					red = (fogColor >> 16 & 255) / 255f;
					green = (fogColor >> 8 & 255) / 255f;
					blue = (fogColor & 255) / 255f;
					lastWaterFogColorUpdateTime = -1L;

					//Sets the fog color.
					RenderSystem.clearColor(red, green, blue, 0.0f);

					ci.cancel();
				}
			}
		}
	}

	@Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
	private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		FluidState submergedFluidState = getSubmergedFluidState(camera);
		FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(submergedFluidState.getFluid());

		if (handler != null) {
			FluidFogHandler fogHandler = handler.getFogHandler(camera.getBlockPos(), submergedFluidState);

			if (fogHandler != null && fogColor != -1) {
				//Gets the fog parameters of the fluid that submerges the camera.
				FogParameters parameters = fogHandler.getFogParameters(camera, fogType, viewDistance, thickFog, tickDelta);

				//Sets the fog parameters.
				RenderSystem.setShaderFogStart(parameters.fogStart());
				RenderSystem.setShaderFogEnd(parameters.fogEnd());
				RenderSystem.setShaderFogShape(parameters.fogShape());

				ci.cancel();
			}
		}
	}

	@Unique
	private static FluidState getSubmergedFluidState(@NotNull Camera camera) {
		//Gets the fluid in which the player (player camera) is submerged at the current position.
		return ((CameraAccessor) camera).getArea().getFluidState(camera.getBlockPos());
	}
}
