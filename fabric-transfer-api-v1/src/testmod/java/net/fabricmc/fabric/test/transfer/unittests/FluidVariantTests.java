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

package net.fabricmc.fabric.test.transfer.unittests;

import static net.fabricmc.fabric.test.transfer.unittests.TestUtil.assertEquals;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

class FluidVariantTests {
	public static void run() {
		testFlowing();
	}

	private static void testFlowing() {
		assertFluidEquals(Fluids.WATER, FluidVariant.of(Fluids.WATER), FluidVariant.of(Fluids.FLOWING_WATER));
		assertFluidEquals(Fluids.LAVA, FluidVariant.of(Fluids.LAVA), FluidVariant.of(Fluids.FLOWING_LAVA));
		assertEquals(FluidVariant.of(Fluids.WATER), FluidVariant.of(Fluids.FLOWING_WATER));
		assertEquals(FluidVariant.of(Fluids.LAVA), FluidVariant.of(Fluids.FLOWING_LAVA));
	}

	private static void assertFluidEquals(Fluid fluid, FluidVariant... variants) {
		for (FluidVariant variant : variants) {
			if (variant.getFluid() != fluid) {
				throw new AssertionError("Variant %s expected to have fluid %s, but found %s".formatted(variant, fluid, variant.getFluid()));
			}
		}
	}
}
