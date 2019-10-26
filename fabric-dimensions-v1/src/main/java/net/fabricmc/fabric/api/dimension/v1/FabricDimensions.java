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

import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

/**
 * This class consists exclusively of static methods that operate on world dimensions.
 */
public final class FabricDimensions {
	private FabricDimensions() {
		throw new AssertionError();
	}

	/**
	 * Teleports an entity to a different dimension, using custom placement logic.
	 *
	 * <p>This method behaves as if:
	 * <pre>{@code teleported.changeDimension(destination)}</pre>
	 *
	 * <p>If {@code destination} is a {@link FabricDimensionType}, the placement logic used
	 * is {@link FabricDimensionType#getDefaultPlacement()}. If {@code destination} is
	 * the nether or the overworld, the default logic is the vanilla path.
	 * For any other dimension, the default placement behaviour is undefined.
	 * When delegating to a placement logic that uses portals, the entity's {@code lastPortalPosition},
	 * {@code lastPortalDirectionVector}, and {@code lastPortalDirection} fields should be updated
	 * before calling this method.
	 *
	 * <p>After calling this method, {@code teleported} may be invalidated. Callers should use
	 * the returned entity for any further manipulation.
	 *
	 * @param teleported  the entity to teleport
	 * @param destination the dimension the entity will be teleported to
	 * @return the teleported entity, or a clone of it
	 * @see #teleport(Entity, DimensionType, EntityPlacer)
	 */
	public static <E extends Entity> E teleport(E teleported, DimensionType destination) {
		return teleport(teleported, destination, null);
	}

	/**
	 * Teleports an entity to a different dimension, using custom placement logic.
	 *
	 * <p>If {@code customPlacement} is {@code null}, this method behaves as if:
	 * <pre>{@code teleported.changeDimension(destination)}</pre>
	 * The {@code customPlacement} may itself return {@code null}, in which case
	 * the default placement logic for that dimension will be run.
	 *
	 * <p>If {@code destination} is a {@link FabricDimensionType}, the default placement logic
	 * is {@link FabricDimensionType#getDefaultPlacement()}. If {@code destination} is the nether
	 * or the overworld, the default logic is the vanilla path.
	 * For any other dimension, the default placement behaviour is undefined.
	 * When delegating to a placement logic that uses portals, the entity's {@code lastPortalPosition},
	 * {@code lastPortalDirectionVector}, and {@code lastPortalDirection} fields should be updated
	 * before calling this method.
	 *
	 * <p>After calling this method, {@code teleported} may be invalidated. Callers should use
	 * the returned entity for any further manipulation.
	 *
	 * @param teleported   the entity to teleport
	 * @param destination  the dimension the entity will be teleported to
	 * @param customPlacer custom placement logic that will run before the default one,
	 *                     or {@code null} to use the dimension's default behavior (see {@link FabricDimensionType#getDefaultPlacement()}).
	 * @param <E>          the type of the teleported entity
	 * @return the teleported entity, or a clone of it
	 * @throws IllegalStateException if this method is called on a client entity
	 * @apiNote this method must be called from the main server thread
	 */
	public static <E extends Entity> E teleport(E teleported, DimensionType destination, /*Nullable*/ EntityPlacer customPlacer) {
		Preconditions.checkState(!teleported.world.isClient, "Entities can only be teleported on the server side");

		return FabricDimensionInternals.changeDimension(teleported, destination, customPlacer);
	}
}
