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

import static net.fabricmc.fabric.test.transfer.TestUtil.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Unit;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

class FluidVariantTests extends AbstractTransferApiTest {
	@BeforeAll
	static void beforeAll() {
		bootstrap();
	}

	@Test
	public void testFlowing() {
		assertFluidEquals(Fluids.WATER, FluidVariant.of(Fluids.WATER), FluidVariant.of(Fluids.FLOWING_WATER));
		assertFluidEquals(Fluids.LAVA, FluidVariant.of(Fluids.LAVA), FluidVariant.of(Fluids.FLOWING_LAVA));
		assertEquals(FluidVariant.of(Fluids.WATER), FluidVariant.of(Fluids.FLOWING_WATER));
		assertEquals(FluidVariant.of(Fluids.LAVA), FluidVariant.of(Fluids.FLOWING_LAVA));
	}

	@Test
	public void testWithChanges() {
		FluidVariant variant = FluidVariant.of(Fluids.WATER, ComponentChanges.builder()
				.add(DataComponentTypes.FIRE_RESISTANT, Unit.INSTANCE)
				.build());

		FluidVariant newVariant = variant.withChanges(ComponentChanges.builder()
				.remove(DataComponentTypes.FIRE_RESISTANT)
				.add(DataComponentTypes.GLIDER, Unit.INSTANCE)
				.build());

		Assertions.assertFalse(
				newVariant.getComponentMap().contains(DataComponentTypes.FIRE_RESISTANT),
				"New variant's FIRE_RESISTANT component was removed, but is still present"
		);

		Assertions.assertTrue(
				newVariant.getComponentMap().contains(DataComponentTypes.GLIDER),
				"New variant's GLIDER component was added, but is not present"
		);
	}

	private static void assertFluidEquals(Fluid fluid, FluidVariant... variants) {
		for (FluidVariant variant : variants) {
			if (variant.getFluid() != fluid) {
				throw new AssertionError("Variant %s expected to have fluid %s, but found %s".formatted(variant, fluid, variant.getFluid()));
			}
		}
	}
}
