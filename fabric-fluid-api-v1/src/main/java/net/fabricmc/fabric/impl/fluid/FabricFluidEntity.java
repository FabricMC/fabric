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

package net.fabricmc.fabric.impl.fluid;

/**
 * Implements some fluid-related entity features.
 */
public interface FabricFluidEntity {
	/**
	 * @return true if the entity is in a fabric_fluid.
	 */
	boolean isInFabricFluid();

	/**
	 * @return true if the entity is in water.
	 */
	boolean isInWater();

	/**
	 * @return true if the entity is submerged in a fabric_fluid.
	 */
	boolean isSubmergedInFabricFluid();

	/**
	 * @return true if the entity is submerged in a swimmable fluid.
	 */
	boolean isSubmergedInSwimmableFluid();

	/**
	 * @return true if the entity is touching a fabric_fluid.
	 */
	boolean isTouchingFabricFluid();

	/**
	 * @return true if the entity is touching a swimmable fluid.
	 */
	boolean isTouchingSwimmableFluid();

	/**
	 * @return the height of the current fabric fluid.
	 */
	double getFabricFluidHeight();
}
