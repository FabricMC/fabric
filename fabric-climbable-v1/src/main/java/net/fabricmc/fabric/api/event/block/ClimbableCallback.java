package net.fabricmc.fabric.api.event.block;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public interface ClimbableCallback {

	Event<ClimbableCallback> EVENT = EventFactory.createArrayBacked(ClimbableCallback.class,
		(listeners) -> ((entity, blockState, pos) -> {
			TriState result = TriState.DEFAULT;

			for (ClimbableCallback event : listeners) {
				TriState triState = event.canClimb(entity, blockState, pos);

				if (triState == TriState.TRUE)
				{
					result = TriState.TRUE;
				}
				else if (triState == TriState.FALSE)
				{
					return TriState.FALSE;
				}
			}

			return result;
		}));

	/**
	 * Determines if the passed LivingEntity can climb the block.
	 *
	 * @param entity The LivingEntity attempting to climb the block.
	 * @param state The BlockState of the block that the entity is attempting to climb.
	 * @param pos The BlockPos of the BlockState.
	 *
	 * @return The return type determines whether the LivingEntity can or cannot climb the
	 * block or if the vanilla checks should be run. Returning TriState.Default will run
	 * the vanilla climbing checks if no other callback returns TriState.True or
	 * TriState.False; Returning TriState.True allows the entity to climb the block if no
	 * other callbacks return TriState.False; Returning TriState.False will prevent the
	 * entity from climbing the block regardless of the result of any other callbacks.
	 */
	TriState canClimb(LivingEntity entity, BlockState state, BlockPos pos);
}
