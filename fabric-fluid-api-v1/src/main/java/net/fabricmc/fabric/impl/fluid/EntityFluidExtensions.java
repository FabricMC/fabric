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

import net.minecraft.fluid.FluidState;

/**
 * Implements some fluid-related entity features.
 */
@SuppressWarnings("unused")
public interface EntityFluidExtensions {
	/**
	 * <p>Updates the current touched fabric fluid state
	 * and applies the fluid pushing strength to the entity.</p>
	 * <br>
	 * <p>NOTE: This is not intended to be public!
	 * Is exposed only for compatibility with LivingEntity mixin!</p>
	 * <p>Call this method with caution!</p>
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	void checkFabricFluidState();

	/**
	 * @return Height of the current fabric fluid.
	 */
	double getFabricFluidHeight();

	/**
	 * @return The current fluid in which the entity is submerged.
	 */
	FluidState getSubmergedFluid();

	/**
	 * @return The first touched fabric fluid of the entity.
	 */
	FluidState getFirstTouchedFabricFluid();

	/**
	 * @return True if the entity is in a fabric fluid.
	 */
	boolean isInFabricFluid();

	/**
	 * @return True if the entity is in water.
	 */
	boolean isInWater();

	/**
	 * @return True if the entity is submerged in a fabric fluid.
	 */
	boolean isSubmergedInFabricFluid();

	/**
	 * @return True if the entity is submerged in a fluid.
	 */
	boolean isSubmergedInFluid();

	/**
	 * @return True if the entity is submerged in a swimmable fluid.
	 */
	boolean isSubmergedInSwimmableFluid();

	/**
	 * @return True if the entity is touching a fabric fluid.
	 */
	boolean isTouchingFabricFluid();

	/**
	 * @return True if the entity is touching a swimmable fluid.
	 */
	boolean isTouchingSwimmableFluid();
}
