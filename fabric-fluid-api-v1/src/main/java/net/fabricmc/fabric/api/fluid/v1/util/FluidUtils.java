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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;
import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;

/**
 * Utilities for fluids.
 */
@SuppressWarnings("unused")
public class FluidUtils {
	/**
	 * Checks if two FluidState are equal.
	 *
	 * @param fluidState1 First FluidState.
	 * @param fluidState2 Second FluidState.
	 * @return True if the two FluidState are equal.
	 */
	public static boolean areEqual(FluidState fluidState1, FluidState fluidState2) {
		return fluidState1 != null && fluidState2 != null && areEqual(fluidState1.getFluid(), fluidState2.getFluid());
	}

	/**
	 * Checks if two Fluid are equal.
	 *
	 * @param fluid1 First Fluid.
	 * @param fluid2 Second Fluid.
	 * @return True if the two Fluid are equal.
	 */
	public static boolean areEqual(Fluid fluid1, Fluid fluid2) {
		return fluid1 != null && fluid2 != null && fluid1.matchesType(fluid2);
	}

	/**
	 * <p>Checks if a FluidState is in a specified tag.</p>
	 * <p>This is null-safe, as it returns false if the FluidState is null.</p>
	 *
	 * @param fluidState FluidState to check.
	 * @param tag Tag to check.
	 * @return True if the FluidState is non-null and is in the specified tag.
	 */
	public static boolean isIn(FluidState fluidState, @NotNull Tag<Fluid> tag) {
		return fluidState != null && fluidState.isIn(tag);
	}

	/**
	 * <p>Checks if a Fluid is in a specified tag.</p>
	 * <p>This is null-safe, as it returns false if the Fluid is null.</p>
	 *
	 * @param fluid Fluid to check.
	 * @param tag Tag to check.
	 * @return True if the Fluid is non-null and is in the specified tag.
	 */
	public static boolean isIn(Fluid fluid, @NotNull Tag<Fluid> tag) {
		return fluid != null && fluid.isIn(tag);
	}

	/**
	 * Checks if the specified entity is touching a fluid with the specified tag.
	 *
	 * @param entity The entity to check.
	 * @param tag    The fluid tag to check.
	 * @return True if the entity is touching a fluid with the specified tag.
	 */
	public static boolean isTouching(@NotNull Entity entity, @NotNull Tag<Fluid> tag) {
		return isTouching(entity.getBoundingBox().contract(0.001D), entity.world, tag);
	}

	/**
	 * Checks if the specified box is touching a fluid with the specified tag.
	 *
	 * @param box   The box to check.
	 * @param world The current world.
	 * @param tag   The fluid tag to check.
	 * @return True if the box is touching a fluid with the specified tag.
	 */
	public static boolean isTouching(@NotNull Box box, @NotNull World world, @NotNull Tag<Fluid> tag) {
		int minX = MathHelper.floor(box.minX);
		int maxX = MathHelper.ceil(box.maxX);
		int minY = MathHelper.floor(box.minY);
		int maxY = MathHelper.ceil(box.maxY);
		int minZ = MathHelper.floor(box.minZ);
		int maxZ = MathHelper.ceil(box.maxZ);

		BlockPos.Mutable pos = new BlockPos.Mutable();

		//Search the first touched fluid with the specified tag
		for (int x = minX; x < maxX; ++x) {
			for (int y = minY; y < maxY; ++y) {
				for (int z = minZ; z < maxZ; ++z) {
					pos.set(x, y, z);
					FluidState fluidState = world.getFluidState(pos);

					if (fluidState.isIn(tag)) {
						double height = y + fluidState.getHeight(world, pos);

						if (height >= box.minY) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Get the fluid in which the entity is submerged.
	 *
	 * @param entity The entity that is supposed to be submerged.
	 * @return Fluid in which the entity is submerged.
	 */
	public static @Nullable FluidState getSubmergedFluid(@NotNull Entity entity) {
		//Get the y of the center of the entity eye
		double eyeY = entity.getEyeY() - 0.1111111119389534d;

		//If the entity is on a boat, is not submerged by nothing, so return null
		if (entity.getVehicle() instanceof BoatEntity) {
			BoatEntity boat = (BoatEntity) entity.getVehicle();

			if (!boat.isSubmergedInWater() && boat.getBoundingBox().maxY >= eyeY && boat.getBoundingBox().minY <= eyeY) {
				return null;
			}
		}

		//Get the fluid in the block at the entity eye position
		BlockPos pos = new BlockPos(entity.getX(), eyeY, entity.getZ());
		FluidState fluidState = entity.world.getFluidState(pos);

		double eyeFluidY = (float) pos.getY() + fluidState.getHeight(entity.world, pos);

		if (eyeFluidY > eyeY) {
			//If the entity is submerged by the fluid above the eye, return the fluid
			return fluidState;
		}

		return null;
	}

	/**
	 * @param state FluidState to check if is breathable (null = air).
	 * @return True if the fluid is breathable.
	 */
	public static boolean isBreathable(FluidState state) {
		return state == null || isBreathable(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is breathable (null = air).
	 * @return True if the fluid is breathable.
	 */
	public static boolean isBreathable(Fluid fluid) {
		return fluid == null || (fluid.isIn(FabricFluidTags.BREATHABLE) || fluid.isIn(FluidTags.LAVA));
	}

	/**
	 * @param state FluidState to check if is breathable by the aquatic mobs.
	 * @return True if the fluid is breathable by the aquatic mobs.
	 */
	public static boolean isBreathableByAquatic(FluidState state) {
		return state != null && isBreathableByAquatic(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is breathable by the aquatic mobs.
	 * @return True if the fluid is breathable by the aquatic mobs.
	 */
	public static boolean isBreathableByAquatic(Fluid fluid) {
		return fluid != null && (fluid.isIn(FabricFluidTags.BREATHABLE_BY_AQUATIC) || fluid.isIn(FluidTags.WATER));
	}

	/**
	 * @param state FluidState to check if is a custom fabric fluid.
	 * @return True if the fluid is a custom fabric fluid.
	 */
	public static boolean isFabricFluid(FluidState state) {
		return state != null && isFabricFluid(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is a custom fabric fluid.
	 * @return True if the fluid is a custom fabric fluid.
	 */
	public static boolean isFabricFluid(Fluid fluid) {
		return fluid instanceof FabricFlowableFluid;
	}

	/**
	 * @param state FluidState to check if is navigable.
	 * @return True if the fluid is navigable.
	 */
	public static boolean isNavigable(FluidState state) {
		return state != null && isNavigable(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is navigable.
	 * @return True if the fluid is navigable.
	 */
	public static boolean isNavigable(Fluid fluid) {
		return fluid != null && (fluid.isIn(FluidTags.WATER) || (isFabricFluid(fluid) && fluid.isIn(FabricFluidTags.NAVIGABLE)));
	}

	/**
	 * @param state FluidState to check if is swimmable.
	 * @return True if the fluid is swimmable.
	 */
	public static boolean isSwimmable(FluidState state) {
		return state != null && isSwimmable(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is swimmable.
	 * @return True if the fluid is swimmable.
	 */
	public static boolean isSwimmable(Fluid fluid) {
		return fluid != null && (fluid.isIn(FluidTags.WATER) || (isFabricFluid(fluid) && fluid.isIn(FabricFluidTags.SWIMMABLE)));
	}
}
