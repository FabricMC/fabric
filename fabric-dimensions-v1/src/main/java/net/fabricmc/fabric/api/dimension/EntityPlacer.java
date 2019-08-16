package net.fabricmc.fabric.api.dimension;

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
     *
     * <p> This method may have side effects such as the creation of a portal in the target dimension,
     * or the creation of a chunk loading ticket.
     *
     * @param portalDir the direction the portal is facing, meaningless if no portal was used
     * @param portalX   the x coordinate of the corner of the portal, 0 if no portal was used
     * @param portalY   the y coordinate of the corner of the portal, 0 if no portal was used
     * @return a teleportation target, or {@code null} to fall back to further handling
     * @apiNote When this method is called, the entity's world is its source dimension.
     */
    /* @Nullable */
    BlockPattern.TeleportTarget placeEntity(Entity teleported, ServerWorld destination, Direction portalDir, double portalX, double portalY);
}
