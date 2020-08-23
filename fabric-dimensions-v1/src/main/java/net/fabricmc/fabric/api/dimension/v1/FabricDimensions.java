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
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

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
	 * Registers a modded dimension type as not intended to show Vanilla's experimental warning when a world is
	 * loaded.
	 *
	 * <p>the given dimension type still has to be defined in a JSON file or otherwise be registered with the game.
	 *
	 * <p>Keep in mind that makes your mod responsible for applying any migrations in future versions that might be
	 * required to load a world using this dimension type, since Vanilla is unlikely to offer automated migrations.
	 *
	 * @see <a href="https://minecraft.gamepedia.com/Custom_dimension">Official wiki page on custom dimensions</a>
	 */
	public static void addStableDimensionType(RegistryKey<DimensionType> key) {
		FabricDimensionInternals.addStableDimensionType(key);
	}

	/**
	 * Registers a modded dimension as not intended to show Vanilla's experimental warning when a world is
	 * loaded. Registry keys for dimension options and worlds will use the same identifiers.
	 *
	 * <p>the given dimension still has to be defined in a JSON file or otherwise be registered with the game.
	 *
	 * <p>Keep in mind that makes your mod responsible for applying any migrations in future versions that might be
	 * required to load a world using this dimension type, since Vanilla is unlikely to offer automated migrations.
	 *
	 * @see <a href="https://minecraft.gamepedia.com/Custom_dimension">Official wiki page on custom dimensions</a>
	 */
	public static void addStableDimension(RegistryKey<DimensionOptions> key) {
		FabricDimensionInternals.addStableDimension(key);
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
	 * @param <E>         the type of the teleported entity
	 * @return Returns the teleported entity in the target dimension, which may be a new entity or <code>teleported</code>,
	 * depending on the entity type.
	 * @throws IllegalStateException if this method is called on a client entity
	 * @apiNote this method must be called from the main server thread
	 */
	public static <E extends Entity> E teleport(E teleported, ServerWorld destination, TeleportTarget target) {
		Preconditions.checkNotNull(target, "A target must be provided");
		Preconditions.checkState(!teleported.world.isClient, "Entities can only be teleported on the server side");

		return FabricDimensionInternals.changeDimension(teleported, destination, target);
	}
}
