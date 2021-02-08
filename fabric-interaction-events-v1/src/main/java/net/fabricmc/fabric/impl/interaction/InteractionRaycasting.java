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

package net.fabricmc.fabric.impl.interaction;

import java.util.Objects;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

/**
 * Utilities related to raycasting in preparation for calling interaction events.
 */
public final class InteractionRaycasting {
	/**
	 * Gets the reach distance of a player.
	 * TODO: This may be nice to provide some api to use this from.
	 *
	 * @param player the player
	 * @return the reach distance
	 */
	public static double getReachDistance(ServerPlayerEntity player) {
		Objects.requireNonNull(player, "Player cannot be null!");

		return player.interactionManager.getGameMode() == GameMode.CREATIVE ? 5.0D : 4.5D;
	}

	public static Vec3d getEyePos(ServerPlayerEntity player) {
		return new Vec3d(player.getX(), player.getY() + player.getStandingEyeHeight(), player.getZ());
	}

	public static Vec3d getEyeEndPos(ServerPlayerEntity player, double distance) {
		float pitch = player.pitch;
		float yaw = player.yaw;
		Vec3d eyePos = getEyePos(player);

		/*
		 * Calculate the end pos for raycasting.
		 * Remember we are in 3 dimensions so we need to calculate the x, y and z offsets.
		 * Just takes a little 3D trig to get those offsets.
		 *
		 * Firstly we know the length of the diagonal, the pitch and the yaw.
		 * So we can then calculate the x, y and z offset of the end position of the raycast.
		 *
		 * We can find the y offset first by using the pitch and calculating the vertical offset.
		 * Then calculate the diagonal horizontal offset from the pitch.
		 *
		 * The diagonal horizontal offset is the hypotenuse of the triangle formed by the yaw.
		 * The diagonal horizontal length can be used to calculate the x and z offsets.
		 */

		// MathHelper works in radians
		final float RADIANS_PER_DEGREE = 0.017453292F;
		// We can use sine to calculate the y offset based on the pitch
		float pitchInRadians = pitch * RADIANS_PER_DEGREE;

		// Multiply by distance to get this in terms of distance and not magnitude
		float yOffset = (float) (MathHelper.sin(pitchInRadians) * distance);
		float diagonalHorizontalLength = (float) (MathHelper.cos(pitchInRadians) * distance);

		float yawInRadians = yaw * RADIANS_PER_DEGREE;

		float sidewaysLengthOverDiagonalHorizontalLength = MathHelper.cos(yawInRadians);
		float sidewaysOffset = sidewaysLengthOverDiagonalHorizontalLength * diagonalHorizontalLength; // Already in terms of full distance

		float forwardLengthOverDiagonalHorizontalLength = MathHelper.sin(yawInRadians);
		float forwardLengthOffset = forwardLengthOverDiagonalHorizontalLength * diagonalHorizontalLength; // Already in terms of full distance

		float xOffset;
		float zOffset;

		// Get what axis we are predominately facing towards
		switch (player.getHorizontalFacing().getAxis()) {
		case X: // Increasing on X axis
			// forwardLength is x offset
			// sidewaysOffset is z offset
			xOffset = forwardLengthOffset;
			zOffset = sidewaysOffset;
			break;
		case Z: // Increasing on Z axis
			// forwardLength is z offset
			// sidewaysOffset is x offset
			zOffset = forwardLengthOffset;
			xOffset = sidewaysOffset;
			break;
		default:
			throw new IllegalStateException("Horizontal facing direction returned vertical axis!");
		}

		return eyePos.add(xOffset, yOffset, zOffset);
	}

	private InteractionRaycasting() {
	}
}
