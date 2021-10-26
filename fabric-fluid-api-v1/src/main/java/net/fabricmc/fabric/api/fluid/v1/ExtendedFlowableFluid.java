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

package net.fabricmc.fabric.api.fluid.v1;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Extends the FlowableFluid class with more customizations.
 */
public interface ExtendedFlowableFluid {
	/**
	 * @return true if the fluid can extinguish fire.
	 */
	boolean canExtinguishFire();

	/**
	 * @return true if the fluid can prevent fall damage.
	 */
	boolean canPreventFallDamage();

	/**
	 * Get the fog color.
	 * @param entity The current entity that displays the fog.
	 */
	int getFogColor(Entity entity);

	/**
	 * Get the fog ending value.
	 * @param entity The current entity that displays the fog.
	 */
	float getFogEnd(Entity entity);

	/**
	 * Get the fog starting value.
	 * @param entity The current entity that displays the fog.
	 */
	float getFogStart(Entity entity);

	/**
	 * Get the fluid viscosity, that is equal to the pushing strength of the fluid.
	 * @param world The current world.
	 * @param pos The position of the current entity.
	 * @param entity The current entity in the fluid.
	 */
	double getViscosity(World world, Vec3d pos, Entity entity);

	/**
	 * Event executed when an entity falls, or enters, into the fluid.
	 * @param world The current world.
	 * @param pos The position of the current entity.
	 * @param entity The current entity in the fluid.
	 */
	void onSplash(World world, Vec3d pos, Entity entity);

	/**
	 * Event executed when the entity is into the fluid.
	 * @param world The current world.
	 * @param entity The current entity in the fluid.
	 */
	void onSubmerged(World world, Entity entity);
}
