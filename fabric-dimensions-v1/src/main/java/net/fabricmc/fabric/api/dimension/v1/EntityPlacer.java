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

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;

/**
 * Responsible for placing an Entity once they have entered a dimension.
 * Stored by a FabricDimensionType, and used in Entity::changeDimension.
 *
 * @see FabricDimensions
 * @see FabricDimensionType
 */
@FunctionalInterface
public interface EntityPlacer {
	/**
	 * Handles the placement of an entity going to a dimension.
	 * Utilized by {@link FabricDimensions#teleport(Entity, DimensionType, EntityPlacer)} to specify placement logic when needed.
	 *
	 * <p>This method may have side effects such as the creation of a portal in the target dimension,
	 * or the creation of a chunk loading ticket.
	 *
	 * @param portalDir        the direction the portal is facing, meaningless if no portal was used
	 * @param horizontalOffset the horizontal offset of the entity relative to the front top left corner of the portal, meaningless if no portal was used
	 * @param verticalOffset   the vertical offset of the entity relative to the front top left corner of the portal, meaningless if no portal was used
	 * @return a teleportation target, or {@code null} to fall back to further handling
	 * @apiNote When this method is called, the entity's world is its source dimension.
	 */
	/* @Nullable */
	BlockPattern.TeleportTarget placeEntity(Entity teleported, ServerWorld destination, Direction portalDir, double horizontalOffset, double verticalOffset);
}
