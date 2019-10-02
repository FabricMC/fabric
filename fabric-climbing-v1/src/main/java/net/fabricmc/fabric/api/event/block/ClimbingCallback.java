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

public interface ClimbingCallback {

	Event<ClimbingCallback> EVENT = EventFactory.createArrayBacked(ClimbingCallback.class,
		(listeners) -> ((entity, blockState, pos) -> {
			Result finalResult = null;

			for (ClimbingCallback listener : listeners) {
				Result result = listener.canClimb(entity, blockState, pos);
				if (result != null) {
					if (finalResult == null || finalResult.priority <= result.priority) {
						finalResult = result;
					}
				}
			}

			return finalResult;
		}));

	/**
	 * Used for applying a non-vanilla climbing speed to the passed living entity.
	 *
	 * <p>The climbing speed of the {@link Result} class determines how fast or slow
	 * the Living Entity will climb; it should be noted that Result has a constructor
	 * that specifies vanilla's default climbing speed of 0.2.</p>
	 *
	 * <p>The priority number allows for your climbing speed to take priority over
	 * the climbing speeds returned by other callbacks. When multiple callbacks
	 * return the same priority number, the last one to return will be used. If this
	 * number is less than 0, the result will be ignored.</p>
	 *
	 * In the event that all callbacks return null; vanilla's default behavior
	 * will be applied.
	 *
	 * @param climber The LivingEntity attempting to climb the block.
	 * @param state The BlockState of the block that the climber is attempting to climb.
	 * @param pos The BlockPos of the BlockState.
	 *
	 * @return A Result instance containing the priority and the climbing speed to set.
	 * Can be null if you don't want to handle a certain situation.
	 */
	Result canClimb(LivingEntity climber, BlockState state, BlockPos pos);

	class Result {
		public final int priority;
		public final double climbSpeed;

		Result(int priority, double climbSpeed) {
			this.priority = priority;
			this.climbSpeed = climbSpeed;
		}

		public Result(int priority) {
			this.priority = priority;
			this.climbSpeed = 0.2D;
		}
	}
}
