package net.fabricmc.fabric.block;

/**
 * Created by RedstoneParadox on 1/6/2019.
 */

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Implement this on blocks that you want an entity to be able to climb.
 */
public interface Climbable {

    /**
     * Method to determine if the block could be climbed.
     *
     * @param livingEntity The LivingEntity that is attempting to climb this block.
     * @param blockState The blockstate of the ladder being climbed.
     * @param pos The position of the block.
     */
    default boolean canClimb(LivingEntity livingEntity, BlockState blockState, BlockPos pos) {
        return true;
    }

    /**
     * The suffix of the death message for this block
     *
     * @return the suffix of the death message.
     */
    default String getDeathSuffix() {
        return null;
    }

    /**
     * Used to determine if this block can be climbed in the same manner as scaffolding.
     *
     * @return if true, LivingEntities will be able to climb this block in the same manner as scaffolding.
     */
    default boolean scaffoldingStyleClimbing() {
        return false;
    }

}
