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

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.dimension.v1.UntargetedTeleportationAttributes;
import net.fabricmc.fabric.api.dimension.v1.UntargetedTeleportationHandler;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

public final class FabricDimensionInternals {
	private FabricDimensionInternals() {
		throw new AssertionError();
	}

	public static final Map<RegistryKey<World>, UntargetedTeleportationHandler> UNTARGETED_HANDLERS = new HashMap<>();

	/**
	 * The attributes passed to the last call to {@link FabricDimensions#teleport(Entity, ServerWorld, TeleportTarget)}.
	 */
	private static UntargetedTeleportationAttributes currentAttributes;

	/**
	 * The target passed to the last call to {@link FabricDimensions#teleport(Entity, ServerWorld, TeleportTarget)}.
	 */
	private static TeleportTarget currentTarget;

	/**
	 * Returns either the targetted teleportation's target location, or lets the target dimension's default
	 * placer decide where to put the entity (if one is registered).
	 */
	public static TeleportTarget getCustomTarget(Entity entity, ServerWorld destination) {
		if (currentTarget != null) {
			return currentTarget; // Custom target always has priority
		}

		UntargetedTeleportationHandler placer = UNTARGETED_HANDLERS.get(destination.getRegistryKey());

		if (placer != null) {
			// Allow the mod to handle placement itself if someone does an untargeted transition
			UntargetedTeleportationAttributes attributes = currentAttributes;

			if (attributes == null) {
				attributes = UntargetedTeleportationAttributes.empty();
			}

			return placer.handleTeleport(entity, destination, attributes);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Entity> E changeDimension(E teleported, ServerWorld dimension, /*Nullable*/ TeleportTarget target,
													   /* Nullable */ UntargetedTeleportationAttributes attributes) {
		Preconditions.checkArgument(!teleported.world.isClient, "Entities can only be teleported on the server side");
		Preconditions.checkArgument(Thread.currentThread() == ((ServerWorld) teleported.world).getServer().getThread(), "Entities must be teleported from the main server thread");

		try {
			currentTarget = target;
			currentAttributes = attributes;
			return (E) teleported.moveToWorld(dimension);
		} finally {
			currentTarget = null;
			currentAttributes = null;
		}
	}
}
