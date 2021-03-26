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

package net.fabricmc.fabric.api.dimension.v1;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

/**
 * This class consists exclusively of static methods that operate on world dimensions.
 *
 * @deprecated Experimental feature, may be removed or changed without further notice due to potential changes to Dimensions in subsequent versions.
 */
@Deprecated
public final class FabricDimensions {
	private FabricDimensions() {
		throw new AssertionError();
	}

	/**
	 * Teleports an entity to a different dimension, placing it at the specified destination.
	 *
	 * <p>Using this method will circumvent Vanilla's portal placement code.
	 *
	 * <p>When teleporting to another dimension, the entity may be replaced with a new entity in the target
	 * dimension. This is not the case for players, but needs to be accounted for by the caller.
	 *
	 * @param teleported  the entity to teleport
	 * @param destination the dimension the entity will be teleported to
	 * @param target      where the entity will be placed in the target world.
	 *                    As in Vanilla, the target's velocity is not applied to players.
	 *                    If target is null, the entity will not be teleported.
	 * @param <E>         the type of the teleported entity
	 * @return Returns the teleported entity in the target dimension, which may be a new entity or <code>teleported</code>,
	 * depending on the entity type.
	 * @throws IllegalStateException if this method is called on a client entity
	 * @apiNote this method must be called from the main server thread
	 */
	@Nullable
	public static <E extends Entity> E teleport(E teleported, ServerWorld destination, TeleportTarget target) {
		Preconditions.checkNotNull(target, "A target must be provided");
		Preconditions.checkState(!teleported.world.isClient, "Entities can only be teleported on the server side");

		return FabricDimensionInternals.changeDimension(teleported, destination, target);
	}
}
