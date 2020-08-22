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

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

/**
 * Implementing this interface allows a dimension to handle teleportation attempts that have no specific target
 * point in the target dimension.
 *
 * <p>It allows the dimension to support deriving the teleportation target from the entities current position,
 * and gives the dimension a chance to place a portal at the target position.
 *
 * @deprecated Experimental feature, may be removed or changed without further notice due to potential changes to Dimensions in subsequent versions.
 *
 * @see FabricDimensions
 */
@Deprecated
@FunctionalInterface
public interface UntargetedTeleportationHandler {
	/**
	 * Handles the placement of an entity going to a dimension without a specific target point.
	 *
	 * <p>This method may have side effects such as the creation of a portal in the target dimension,
	 * or the creation of a chunk loading ticket.
	 *
	 * @param teleported The entity that is being teleported.
	 * @param destination The destination world.
	 * @param attributes This map may contain additional attributes for the teleportation attempt, such as
	 *                   a request to not spawn a portal. See {@link UntargetedTeleportationAttributes} for
	 *                   standard attributes.
	 *
	 * @return a teleportation target, or {@code null} to delegate to vanilla, which will cancel the teleportation
	 * for custom dimensions.
	 * @apiNote When this method is called, the entity's world is its source dimension.
	 */
	/* @Nullable */
	TeleportTarget handleTeleport(Entity teleported, ServerWorld destination, UntargetedTeleportationAttributes attributes);
}
