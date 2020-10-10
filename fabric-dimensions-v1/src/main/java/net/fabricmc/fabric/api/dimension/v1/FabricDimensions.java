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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

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
	 * Teleports an entity to a different dimension, using custom placement logic.
	 *
	 * <p>This method behaves as if:
	 * <pre>{@code teleported.changeDimension(destination)}</pre>
	 *
	 * <p>If {@code destination} has a default placer, that placer will be used. If {@code destination} is
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
	 * @see #teleport(Entity, ServerWorld)
	 */
	public static <E extends Entity> E teleport(E teleported, ServerWorld destination) {
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
	 * <p>If {@code destination} has a default placer, that placer will be used. If {@code destination} is
	 * the nether or the overworld, the default logic is the vanilla path.
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
	 *                     or {@code null} to use the dimension's default behavior.
	 * @param <E>          the type of the teleported entity
	 * @return the teleported entity, or a clone of it
	 * @throws IllegalStateException if this method is called on a client entity
	 * @apiNote this method must be called from the main server thread
	 */
	public static <E extends Entity> E teleport(E teleported, ServerWorld destination, /*Nullable*/ EntityPlacer customPlacer) {
		Preconditions.checkState(!teleported.world.isClient, "Entities can only be teleported on the server side");

		return FabricDimensionInternals.changeDimension(teleported, destination, customPlacer);
	}

	/**
	 * Register a default placer for a dimension, this is used when an entity is teleported to a dimension without
	 * a specified {@link EntityPlacer}.
	 *
	 * @param registryKey The dimension {@link RegistryKey}
	 * @param entityPlacer The {@link EntityPlacer}
	 */
	public static void registerDefaultPlacer(RegistryKey<World> registryKey, EntityPlacer entityPlacer) {
		Preconditions.checkState(!FabricDimensionInternals.DEFAULT_PLACERS.containsKey(registryKey), "Only 1 EntityPlacer can be registered per dimension");
		Preconditions.checkState(!registryKey.getValue().getNamespace().equals("minecraft"), "Minecraft dimensions cannot have a default placer");

		FabricDimensionInternals.DEFAULT_PLACERS.put(registryKey, entityPlacer);
	}
}
