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

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
	 * An event that checks whether a player can start to sleep in a bed-like block.
	 *
	 * @see PlayerEntity#trySleep(BlockPos)
	 */
	public static final Event<AllowSleeping> ALLOW_SLEEPING = EventFactory.createArrayBacked(AllowSleeping.class, callbacks -> (player, sleepingPos) -> {
		for (AllowSleeping callback : callbacks) {
			PlayerEntity.SleepFailureReason reason = callback.allowSleep(player, sleepingPos);

			if (reason != null) {
				return reason;
			}
		}

		return null;
	});

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
	 *
	 * <p>Used for checking whether the block at the current sleeping position is a valid bed block.
	 * If false, the player wakes up.
	 *
	 * @see LivingEntity#isSleepingInBed()
	 */
	public static final Event<AllowBed> ALLOW_BED = EventFactory.createArrayBacked(AllowBed.class, callbacks -> (entity, sleepingPos, state) -> {
		for (AllowBed callback : callbacks) {
			ActionResult result = callback.allowBed(entity, sleepingPos, state);

			if (result != ActionResult.PASS) {
				return result;
			}
		}

		return ActionResult.PASS;
	});

	/**
	 * An event that checks whether the current time of day is valid for sleeping.
	 */
	public static final Event<AllowSleepTime> ALLOW_SLEEP_TIME = EventFactory.createArrayBacked(AllowSleepTime.class, callbacks -> (player, sleepingPos, vanillaResult) -> {
		for (AllowSleepTime callback : callbacks) {
			ActionResult result = callback.allowSleepTime(player, sleepingPos, vanillaResult);

			if (result != ActionResult.PASS) {
				return result;
			}
		}

		return ActionResult.PASS;
	});

	/**
	 * An event that checks whether players can sleep when monsters are nearby.
	 *
	 * <p>This event can also be used to force a failing result, meaning it can do custom monster checks.
	 */
	public static final Event<AllowNearbyMonsters> ALLOW_NEARBY_MONSTERS = EventFactory.createArrayBacked(AllowNearbyMonsters.class, callbacks -> (player, sleepingPos, vanillaResult) -> {
		for (AllowNearbyMonsters callback : callbacks) {
			ActionResult result = callback.allowNearbyMonsters(player, sleepingPos, vanillaResult);

			if (result != ActionResult.PASS) {
				return result;
			}
		}

		return ActionResult.PASS;
	});

	/**
	 * An event that checks whether a sleeping player counts into skipping the current day and resetting the time to 0.
	 */
	public static final Event<AllowResettingTime> ALLOW_RESETTING_TIME = EventFactory.createArrayBacked(AllowResettingTime.class, callbacks -> player -> {
		for (AllowResettingTime callback : callbacks) {
			if (!callback.allowResettingTime(player)) {
				return false;
			}
		}

		return true;
	});

	/**
	 * An event that can be used to provide the entity's sleep direction if missing.
	 *
	 * <p>This is useful for custom bed blocks that need to determine the sleeping direction themselves.
	 */
	public static final Event<ModifySleepingDirection> MODIFY_SLEEPING_DIRECTION = EventFactory.createArrayBacked(ModifySleepingDirection.class, callbacks -> (entity, sleepingPos, sleepingDirection) -> {
		for (ModifySleepingDirection callback : callbacks) {
			sleepingDirection = callback.modifySleepDirection(entity, sleepingPos, sleepingDirection);
		}

		return sleepingDirection;
	});

	/**
	 * An event that checks whether a player's spawn can be set when sleeping.
	 */
	public static final Event<AllowSettingSpawn> ALLOW_SETTING_SPAWN = EventFactory.createArrayBacked(AllowSettingSpawn.class, callbacks -> (player, sleepingPos) -> {
		for (AllowSettingSpawn callback : callbacks) {
			if (!callback.allowSettingSpawn(player, sleepingPos)) {
				return false;
			}
		}

		return true;
	});

	@FunctionalInterface
	public interface AllowSleeping {
		/**
		 * Checks whether a player can start sleeping in a bed-like block.
		 *
		 * @param player      the sleeping player
		 * @param sleepingPos the future {@linkplain LivingEntity#getSleepingPosition() sleeping position} of the entity
		 * @return null if the player can sleep, or a failure reason if they cannot
		 * @see PlayerEntity#trySleep(BlockPos)
		 */
		@Nullable
		PlayerEntity.SleepFailureReason allowSleep(PlayerEntity player, BlockPos sleepingPos);
	}

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
		 * @param entity the sleeping entity
		 */
		void onWakeUp(LivingEntity entity);
	}

	@FunctionalInterface
	public interface AllowBed {
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
		ActionResult allowBed(LivingEntity entity, BlockPos sleepingPos, BlockState state);
	}

	@FunctionalInterface
	public interface AllowSleepTime {
		/**
		 * Checks whether the current time of day is valid for sleeping.
		 *
		 * <p>Non-{@linkplain ActionResult#PASS passing} return values cancel further callbacks.
		 *
		 * @param player        the sleeping player
		 * @param sleepingPos   the (possibly still unset) {@linkplain LivingEntity#getSleepingPosition() sleeping position} of the player
		 * @param vanillaResult true if vanilla allows the time, false otherwise
		 * @return {@link ActionResult#SUCCESS} if the time is valid, {@link ActionResult#FAIL} if it's not,
		 *         {@link ActionResult#PASS} to fall back to other callbacks
		 */
		ActionResult allowSleepTime(PlayerEntity player, BlockPos sleepingPos, boolean vanillaResult);
	}

	@FunctionalInterface
	public interface AllowNearbyMonsters {
		/**
		 * Checks whether a player can sleep when monsters are nearby.
		 *
		 * <p>Non-{@linkplain ActionResult#PASS passing} return values cancel further callbacks.
		 *
		 * @param player        the sleeping player
		 * @param sleepingPos   the (possibly still unset) {@linkplain LivingEntity#getSleepingPosition() sleeping position} of the player
		 * @param vanillaResult true if vanilla's monster check succeeded, false otherwise
		 * @return {@link ActionResult#SUCCESS} to allow sleeping, {@link ActionResult#FAIL} to prevent sleeping,
		 *         {@link ActionResult#PASS} to fall back to other callbacks
		 */
		ActionResult allowNearbyMonsters(PlayerEntity player, BlockPos sleepingPos, boolean vanillaResult);
	}

	@FunctionalInterface
	public interface AllowResettingTime {
		/**
		 * Checks whether a sleeping player counts into skipping the current day and resetting the time to 0.
		 *
		 * @param player        the sleeping player
		 * @return true if allowed, false otherwise
		 */
		boolean allowResettingTime(PlayerEntity player);
	}

	@FunctionalInterface
	public interface ModifySleepingDirection {
		/**
		 * Modifies or provides a sleeping direction for a block.
		 *
		 * @param entity            the sleeping entity
		 * @param sleepingPos       the position of the block slept on
		 * @param sleepingDirection the old sleeping direction, or null if not determined by vanilla logic
		 * @return the new sleeping direction
		 */
		@Nullable
		Direction modifySleepDirection(LivingEntity entity, BlockPos sleepingPos, @Nullable Direction sleepingDirection);
	}

	@FunctionalInterface
	public interface AllowSettingSpawn {
		/**
		 * Checks whether a player's spawn can be set when sleeping.
		 *
		 * @param player      the sleeping player
		 * @param sleepingPos the sleeping position
		 * @return true if allowed, false otherwise
		 */
		boolean allowSettingSpawn(PlayerEntity player, BlockPos sleepingPos);
	}

	private EntitySleepEvents() {
	}
}
