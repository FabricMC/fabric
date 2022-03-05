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
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

/**
 * Defines a non-transparent fluid with a fog.
 */
public interface OpaqueFluid {
	/**
	 * Gets the color of the fluid fog.
	 *
	 * @param entity    Entity submerged by the fluid.
	 * @param tickDelta Time passed from the last tick.
	 * @param world     The current client-side world.
	 * @return <p>An int value indicating the color of the fluid fog.</p>
	 * <p>If the color is -1 no fog will be rendered.</p>
	 * <p>(It could be a hexadecimal value like 0xFFFFFF).</p>
	 */
	int getFabricFogColor(Entity entity, float tickDelta, ClientWorld world);

	/**
	 * Gets the start point of the fluid fog.
	 *
	 * @param entity       Entity submerged by the fluid.
	 * @param fogType      Type of the fog (SKY or TERRAIN).
	 * @param viewDistance View distance of the entity.
	 * @param thickFog     Specifies if the fog is thick.
	 * @return A float indicating the start point of the fluid fog from the entity.
	 */
	float getFabricFogStart(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog);

	/**
	 * Gets the end point of the fluid fog.
	 *
	 * @param entity       Entity submerged by the fluid.
	 * @param fogType      Type of the fog (SKY or TERRAIN).
	 * @param viewDistance View distance of the entity.
	 * @param thickFog     Specifies if the fog is thick.
	 * @return A float indicating the end point of the fluid fog from the entity.
	 */
	float getFabricFogEnd(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog);

	/**
	 * Gets the shape of the fluid fog.
	 *
	 * @param entity       Entity submerged by the fluid.
	 * @param fogType      Type of the fog (SKY or TERRAIN).
	 * @param viewDistance View distance of the entity.
	 * @param thickFog     Specifies if the fog is thick.
	 * @return An enum value indicating the shape of the fluid fog: CYLINDER or SPHERE.
	 */
	FogShape getFabricFogShape(Entity entity, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog);
}
