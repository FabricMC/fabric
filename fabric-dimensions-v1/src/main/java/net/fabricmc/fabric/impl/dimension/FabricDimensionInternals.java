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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

public final class FabricDimensionInternals {
	/**
	 * The target passed to the last call to {@link FabricDimensions#teleport(Entity, ServerWorld, TeleportTarget)}.
	 */
	private static TeleportTarget currentTarget;

	private FabricDimensionInternals() {
		throw new AssertionError();
	}

	/**
	 * Returns the last target set when a user of the API requested teleportation, or null.
	 */
	public static TeleportTarget getCustomTarget() {
		return currentTarget;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Entity> E changeDimension(E teleported, ServerWorld dimension, TeleportTarget target) {
		Preconditions.checkArgument(!teleported.world.isClient, "Entities can only be teleported on the server side");
		Preconditions.checkArgument(Thread.currentThread() == ((ServerWorld) teleported.world).getServer().getThread(), "Entities must be teleported from the main server thread");

		try {
			currentTarget = target;
			return (E) teleported.moveToWorld(dimension);
		} finally {
			currentTarget = null;
		}
	}
}
