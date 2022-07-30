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

package net.fabricmc.fabric.api.client.render.fluid.v1;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;

/**
 * Interface for handling the fog rendering settings of a fluid.
 */
public interface FluidFogHandler {
	/**
	 * Gets the fluid fog color RGB. Alpha is ignored.
	 *
	 * @param camera    Camera submerged by the fluid.
	 * @param tickDelta Time passed from the last tick.
	 * @param world     Current client world.
	 * @return the color of the fog, or {@code -1} to make it hidden.
	 */
	int getFogColor(Camera camera, float tickDelta, ClientWorld world);

	/**
	 * Gets the distance in blocks, from the camera position, in which the fog starts rendering.
	 *
	 * <p>This could be negative, in this case the fog starts partially opaque.
	 *
	 * @param camera       Camera submerged by the fluid.
	 * @param fogType      Type of fog.
	 * @param viewDistance Current view distance of the submerged player.
	 * @param thickFog     Specifies if a thick fog must be rendered.
	 */
	float getFogStartRadius(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog);

	/**
	 * Gets the distance in blocks, from the camera position, after which the fog is totally opaque.
	 *
	 * <p>If this is less than the start distance, will be ignored.
	 *
	 * @param camera       Camera submerged by the fluid.
	 * @param fogType      Type of fog.
	 * @param viewDistance Current view distance of the submerged player.
	 * @param thickFog     Specifies if a thick fog must be rendered.
	 */
	float getFogEndRadius(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog);

	/**
	 * Gets the shape of the fluid fog.
	 *
	 * @param camera       Camera submerged by the fluid.
	 * @param fogType      Type of fog.
	 * @param viewDistance Current view distance of the submerged player.
	 * @param thickFog     Specifies if a thick fog must be rendered.
	 */
	FogShape getFogShape(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog);
}
