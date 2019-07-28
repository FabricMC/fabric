/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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
	 * Determines if the passed LivingEntity can or cannot climb the block or if the
	 * vanilla checks should be run. Returning TriState.Default will run
	 * the vanilla climbing checks if no other callback returns TriState.True or
	 * TriState.False; Returning TriState.True allows the climber to climb the block if no
	 * other callbacks return TriState.False; Returning TriState.False will prevent the
	 * climber from climbing the block regardless of the result of any other callbacks.
	 *
	 * @param climber The LivingEntity attempting to climb the block.
	 * @param state The BlockState of the block that the climber is attempting to climb.
	 * @param pos The BlockPos of the BlockState.
	 *
	 * @return The TriState result.
	 */
	TriState canClimb(LivingEntity climber, BlockState state, BlockPos pos);
}
