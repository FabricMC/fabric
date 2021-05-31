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

package net.fabricmc.fabric.api.transfer.v1.fluid;

/**
 * Preconditions for fluid transfer.
 */
public class FluidPreconditions {
	/**
	 * Ensure that the passed fluid is not empty.
	 *
	 * @throws IllegalArgumentException If the fluid is empty.
	 */
	public static void notEmpty(FluidKey fluid) {
		if (fluid.isEmpty()) {
			throw new IllegalArgumentException("Fluid may not be empty.");
		}
	}

	/**
	 * Ensure that the passed fluid is not empty and that the passed amount is not negative.
	 *
	 * @throws IllegalArgumentException If the fluid is empty or if the amount is negative.
	 */
	public static void notEmptyNotNegative(FluidKey fluid, long amount) {
		FluidPreconditions.notEmpty(fluid);

		if (amount < 0) {
			throw new IllegalArgumentException("Amount may not be negative, but it is: " + amount);
		}
	}

	private FluidPreconditions() {
	}
}
