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

package net.fabricmc.fabric.test.fluid.block;

import net.minecraft.block.FluidBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.fluid.v1.FabricFluidBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.test.fluid.core.ModCore;
import net.fabricmc.fabric.test.fluid.fluid.MFluids;

public class MBlocks {
	public static final Identifier BLUE_FLUID_ID = new Identifier(ModCore.ID, "blue_fluid");
	public static final FluidBlock BLUE_FLUID = Registry.register(Registry.BLOCK, BLUE_FLUID_ID,
			new FabricFluidBlock(MFluids.BLUE_FLUID, FabricBlockSettings.of(Material.WATER).mapColor(MapColor.BLUE)));

	public static final Identifier CYAN_FLUID_ID = new Identifier(ModCore.ID, "cyan_fluid");
	public static final FluidBlock CYAN_FLUID = Registry.register(Registry.BLOCK, CYAN_FLUID_ID,
			new FabricFluidBlock(MFluids.CYAN_FLUID, FabricBlockSettings.of(Material.WATER).mapColor(MapColor.CYAN)));

	public static final Identifier GREEN_FLUID_ID = new Identifier(ModCore.ID, "green_fluid");
	public static final FluidBlock GREEN_FLUID = Registry.register(Registry.BLOCK, GREEN_FLUID_ID,
			new FabricFluidBlock(MFluids.GREEN_FLUID, FabricBlockSettings.of(Material.WATER).mapColor(MapColor.GREEN)));

	public static final Identifier RED_FLUID_ID = new Identifier(ModCore.ID, "red_fluid");
	public static final FluidBlock RED_FLUID = Registry.register(Registry.BLOCK, RED_FLUID_ID,
			new FabricFluidBlock(MFluids.RED_FLUID, FabricBlockSettings.of(Material.WATER).mapColor(MapColor.RED)));

	public static void load() {
	}
}
