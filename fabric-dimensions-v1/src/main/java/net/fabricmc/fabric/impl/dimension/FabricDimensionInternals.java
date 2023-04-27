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

package net.fabricmc.fabric.impl.dimension;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

public final class FabricDimensionInternals {
	private FabricDimensionInternals() {
		throw new AssertionError();
	}

	@SuppressWarnings("unchecked")
	public static <E extends Entity> E changeDimension(E teleported, ServerWorld dimension, TeleportTarget target) {
		Preconditions.checkArgument(!teleported.getWorld().isClient, "Entities can only be teleported on the server side");
		Preconditions.checkArgument(Thread.currentThread() == ((ServerWorld) teleported.getWorld()).getServer().getThread(), "Entities must be teleported from the main server thread");

		try {
			((Teleportable) teleported).fabric_setCustomTeleportTarget(target);

			// Fast path for teleporting within the same dimension.
			if (teleported.getWorld() == dimension) {
				if (teleported instanceof ServerPlayerEntity serverPlayerEntity) {
					serverPlayerEntity.networkHandler.requestTeleport(target.position.x, target.position.y, target.position.z, target.yaw, teleported.getPitch());
				} else {
					teleported.refreshPositionAndAngles(target.position.x, target.position.y, target.position.z, target.yaw, teleported.getPitch());
				}

				teleported.setVelocity(target.velocity);
				teleported.setHeadYaw(target.yaw);

				return teleported;
			}

			return (E) teleported.moveToWorld(dimension);
		} finally {
			((Teleportable) teleported).fabric_setCustomTeleportTarget(null);
		}
	}
}
