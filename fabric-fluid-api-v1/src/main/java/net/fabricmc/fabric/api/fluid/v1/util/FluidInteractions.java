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
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Utilities about interactions with fluids.
 */
@SuppressWarnings("unused")
public class FluidInteractions {
	private static final double ENTITY_EYE_CENTER_OFFSET = 0.1111111119389534D;

	/**
	 * Get the fluid in which the entity is submerged.
	 *
	 * @param entity The entity that is supposed to be submerged.
	 * @return Fluid in which the entity is submerged.
	 */
	public static @Nullable FluidState getSubmergedFluid(@NotNull Entity entity) {
		//This method is inspired from the updateSubmergedInWaterState method in Entity class.

		//Get the y of the center of the entity eye
		double eyeY = entity.getEyeY() - ENTITY_EYE_CENTER_OFFSET;

		//If the entity is on a boat, is not submerged by nothing, so return null
		if (entity.getVehicle() instanceof BoatEntity boat) {
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
		//This method is inspired from the updateMovementInFluid method in Entity class.

		//Gets the coordinates of the box edges
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
}
