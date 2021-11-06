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

package net.fabricmc.fabric.api.fluid.v1.util;

import net.fabricmc.fabric.api.fluid.v1.ExtendedFlowableFluid;
import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for fluids.
 */
public class FluidUtils {
	/**
	 * @param state FluidState to check if is navigable.
	 * @return true if the fluid is navigable.
	 */
	public static boolean isNavigable(@NotNull FluidState state) {
		return isNavigable(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is navigable.
	 * @return true if the fluid is navigable.
	 */
	public static boolean isNavigable(@NotNull Fluid fluid) {
		return fluid.isIn(FabricFluidTags.FABRIC_FLUID)
				//By default, all fabric_fluid are navigable
				? !(fluid instanceof ExtendedFlowableFluid eFluid) || eFluid.isNavigable()
				: fluid.isIn(FluidTags.WATER);
	}

	/**
	 * @param state FluidState to check if is swimmable.
	 * @return true if the fluid is swimmable.
	 */
	public static boolean isSwimmable(@NotNull FluidState state) {
		return isSwimmable(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is swimmable.
	 * @return true if the fluid is swimmable.
	 */
	public static boolean isSwimmable(@NotNull Fluid fluid) {
		return fluid.isIn(FabricFluidTags.FABRIC_FLUID)
				//By default, all fabric_fluid are swimmable
				? !(fluid instanceof ExtendedFlowableFluid eFluid) || eFluid.isSwimmable()
				: fluid.isIn(FluidTags.WATER) || fluid.isIn(FluidTags.LAVA);
	}

	/**
	 * Check if an entity is touching a fluid with the specified tag.
	 * @param entity The entity to check.
	 * @param tag The fluid tag to search.
	 * @return true if the specified entity is touching a fluid with the specified tag.
	 */
	public static @Nullable FluidState getFirstTouchedFluid(@NotNull Entity entity, Tag<Fluid> tag) {
		return getFirstTouchedFluid(entity.getBoundingBox().contract(0.001D), entity.world, tag);
	}

	/**
	 * Check if a box is touching a fluid with the specified tag.
	 * @param box The box to check.
	 * @param world The current world.
	 * @param tag The fluid tag to search.
	 * @return true if the specified box is touching a fluid with the specified tag.
	 */
	public static @Nullable FluidState getFirstTouchedFluid(@NotNull Box box, World world, Tag<Fluid> tag) {
		int minX = MathHelper.floor(box.minX);
		int maxX = MathHelper.ceil(box.maxX);
		int minY = MathHelper.floor(box.minY);
		int maxY = MathHelper.ceil(box.maxY);
		int minZ = MathHelper.floor(box.minZ);
		int maxZ = MathHelper.ceil(box.maxZ);

		BlockPos.Mutable pos = new BlockPos.Mutable();

		//Search the first touched fluid with the specified tag
		for(int x = minX; x < maxX; ++x) {
			for(int y = minY; y < maxY; ++y) {
				for(int z = minZ; z < maxZ; ++z) {
					pos.set(x, y, z);
					FluidState fluidState = world.getFluidState(pos);
					if (fluidState.isIn(tag)) {
						double height = y + fluidState.getHeight(world, pos);
						if (height >= box.minY) {
							return fluidState;
						}
					}
				}
			}
		}

		return null;
	}
}
