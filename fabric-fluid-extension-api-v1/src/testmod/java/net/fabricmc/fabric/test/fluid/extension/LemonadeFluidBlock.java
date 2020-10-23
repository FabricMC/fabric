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

package net.fabricmc.fabric.test.fluid.extension;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;

import net.fabricmc.fabric.api.fluid.extension.v1.FabricFlowableFluid;
import net.fabricmc.fabric.api.fluid.extension.v1.FabricFlowableFluidBlock;

public class LemonadeFluidBlock extends FabricFlowableFluidBlock {
	public static Material LEMONADE_MATERIAL = new Material(MaterialColor.YELLOW, true, false, false, false, false, true, PistonBehavior.DESTROY);

	public LemonadeFluidBlock() {
		super(AbstractBlock.Settings.of(LEMONADE_MATERIAL).noCollision().ticksRandomly().strength(100.0F).luminance((state) -> {
			return 15;
		}).dropsNothing());
	}

	@Override
	public FabricFlowableFluid getFluid() {
		return FluidTest.LEMONADE_FLUID;
	}
}
