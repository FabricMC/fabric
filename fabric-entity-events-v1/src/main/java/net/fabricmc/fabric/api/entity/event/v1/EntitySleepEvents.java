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

package net.fabricmc.fabric.api.entity.event.v1;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events about the sleep of {@linkplain LivingEntity living entities}.
 */
public final class EntitySleepEvents {
	/**
	 * An event that is called when an entity starts to sleep.
	 */
	public static final Event<StartSleeping> START_SLEEPING = EventFactory.createArrayBacked(StartSleeping.class, callbacks -> (entity, sleepingPos) -> {
		for (StartSleeping callback : callbacks) {
			callback.onSleep(entity, sleepingPos);
		}
	});

	/**
	 * An event that is called when an entity wakes up.
	 */
	public static final Event<WakeUp> WAKE_UP = EventFactory.createArrayBacked(WakeUp.class, callbacks -> (entity) -> {
		for (WakeUp callback : callbacks) {
			callback.onWakeUp(entity);
		}
	});

	/**
	 * An event that is called to check whether a block is valid for sleeping.
	 */
	public static final Event<IsValidBed> IS_VALID_BED = EventFactory.createArrayBacked(IsValidBed.class, callbacks -> (entity, sleepingPos, state) -> {
		for (IsValidBed callback : callbacks) {
			ActionResult result = callback.isValidBed(entity, sleepingPos, state);

			if (result != ActionResult.PASS) {
				return result;
			}
		}

		return ActionResult.PASS;
	});

	/**
	 * An event that can be used to provide the entity's sleep direction if missing.
	 *
	 * <p>This is useful for custom bed blocks that need to determine the sleeping direction themselves.
	 */
	public static final Event<ModifySleepingDirection> MODIFY_SLEEPING_DIRECTION = EventFactory.createArrayBacked(ModifySleepingDirection.class, callbacks -> (entity, sleepingPos, sleepingDirection) -> {
		for (ModifySleepingDirection callback : callbacks) {
			Optional<Direction> result = callback.modifySleepDirection(entity, sleepingPos, sleepingDirection);

			if (result.isPresent()) {
				return result;
			}
		}

		return Optional.empty();
	});

	@FunctionalInterface
	public interface StartSleeping {
		/**
		 * Called when an entity starts to sleep.
		 *
		 * @param entity      the sleeping entity
		 * @param sleepingPos the {@linkplain LivingEntity#getSleepingPosition() sleeping position} of the entity
		 */
		void onSleep(LivingEntity entity, BlockPos sleepingPos);
	}

	@FunctionalInterface
	public interface WakeUp {
		/**
		 * Called when an entity wakes up.
		 *
		 * @param entity      the sleeping entity
		 */
		void onWakeUp(LivingEntity entity);
	}

	@FunctionalInterface
	public interface IsValidBed {
		/**
		 * Checks whether a block is a valid bed for the entity.
		 *
		 * <p>Non-{@linkplain ActionResult#PASS passing} return values cancel further callbacks.
		 *
		 * @param entity      the sleeping entity
		 * @param sleepingPos the position of the block
		 * @param state       the block state to check
		 * @return {@link ActionResult#SUCCESS} if the bed is valid, {@link ActionResult#FAIL} if it's not,
		 *         {@link ActionResult#PASS} to fall back to other callbacks
		 */
		ActionResult isValidBed(LivingEntity entity, BlockPos sleepingPos, BlockState state);
	}

	@FunctionalInterface
	public interface ModifySleepingDirection {
		/**
		 * Modifies or provides a sleeping direction for a block.
		 *
		 * <p>A present return value cancels further callbacks.
		 *
		 * @param entity            the sleeping entity
		 * @param sleepingPos       the position of the block slept on
		 * @param sleepingDirection the old sleeping direction, or null if not determined by vanilla logic
		 * @return a new direction, or {@link Optional#empty()} to fall back to other callbacks
		 */
		Optional<Direction> modifySleepDirection(LivingEntity entity, BlockPos sleepingPos, @Nullable Direction sleepingDirection);
	}

	private EntitySleepEvents() {
	}
}
