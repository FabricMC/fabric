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

package net.fabricmc.fabric.test.client.rendering.fluid;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidFogHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FogParameters;

public class CustomizedFogHandler implements FluidFogHandler {
	private final FogParameters parameters;

	public CustomizedFogHandler(float startRadius, float endRadius) {
		this.parameters = new FogParameters(startRadius, endRadius, FogShape.SPHERE);
	}

	@Override
	public int getFogColor(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness) {
		// Gets the water fog color from the current biome, based on the player (player camera) position
		return world.getBiome(new BlockPos(camera.getPos())).value().getWaterFogColor();
	}

	@Override
	public FogParameters getFogParameters(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta) {
		return parameters;
	}
}
