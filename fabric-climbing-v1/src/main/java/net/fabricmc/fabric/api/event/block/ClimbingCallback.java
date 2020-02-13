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

package net.fabricmc.fabric.api.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface ClimbingCallback {
	Event<ClimbingCallback> EVENT = EventFactory.createArrayBacked(ClimbingCallback.class,
			(listeners) -> ((entity, blockState, pos) -> {
				Double finalSpeed = null;

				for (ClimbingCallback listener : listeners) {
					Double speed = listener.canClimb(entity, blockState, pos);
					if (speed != null) finalSpeed = speed;
				}

				return finalSpeed;
			}));

	/**
	 * Used for applying a non-vanilla climbing speed to the passed living entity.
	 *
	 * <p>The climbing speed returned determines how fast or slow the
	 * Living Entity will climb. Can be null if you don't want to handle the given
	 * situation.</p>
	 *
	 * <p>In the event that all callbacks return null; vanilla's default behavior
	 * will be applied. </p>
	 *
	 * @param climber The LivingEntity attempting to climb the block.
	 * @param state The BlockState of the block that the climber is attempting to climb.
	 * @param pos The BlockPos of the BlockState.
	 *
	 * @return The desired climbing speed or null.
	 */
	Double canClimb(LivingEntity climber, BlockState state, BlockPos pos);
}
