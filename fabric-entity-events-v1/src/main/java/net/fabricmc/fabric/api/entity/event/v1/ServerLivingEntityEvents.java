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

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Various server-side only events related to living entities.
 */
public final class ServerLivingEntityEvents {
	/**
	 * An event that is called when a living entity is going to take damage.
	 * This is fired from {@link LivingEntity#damage}, before armor or any other mitigation are applied.
	 * Mods can cancel this to prevent the damage entirely.
	 */
	public static final Event<AllowDamage> ALLOW_DAMAGE = EventFactory.createArrayBacked(AllowDamage.class, callbacks -> (entity, source, amount) -> {
		for (AllowDamage callback : callbacks) {
			if (!callback.allowDamage(entity, source, amount)) {
				return false;
			}
		}

		return true;
	});

	/**
	 * An event that is called when an entity takes fatal damage.
	 *
	 * <p>Mods can cancel this to keep the entity alive.
	 *
	 * <p>Vanilla checks for entity health {@code <= 0} each tick (with {@link LivingEntity#isDead()}), and kills if true -
	 * so the entity will still die next tick if this event is cancelled.
	 * It's assumed that the listener will do something to prevent this, for example, if the entity is a player:
	 * <ul>
	 *     <li>a minigame mod teleporting the player into a 'respawn room' and setting their health to 20.0</li>
	 *     <li>a mod that changes death mechanics switching the player over to the mod's play-mode, where death doesn't apply</li>
	 * </ul>
	 */
	public static final Event<AllowDeath> ALLOW_DEATH = EventFactory.createArrayBacked(AllowDeath.class, callbacks -> (entity, damageSource, damageAmount) -> {
		for (AllowDeath callback : callbacks) {
			if (!callback.allowDeath(entity, damageSource, damageAmount)) {
				return false;
			}
		}

		return true;
	});

	/**
	 * An event that is called when a living entity dies.
	 */
	public static final Event<AfterDeath> AFTER_DEATH = EventFactory.createArrayBacked(AfterDeath.class, callbacks -> (entity, damageSource) -> {
		for (AfterDeath callback : callbacks) {
			callback.afterDeath(entity, damageSource);
		}
	});

	/**
	 * An event that is called when minecraft checks if an entity is at a climbable block position.
	 * This is fired before minecraft does its own checks, so it's not guaranteed that block at pos
	 * is in {@link net.minecraft.registry.tag.BlockTags#CLIMBABLE}.
	 *
	 * <p>If your mod has special climbing behaviour, you should use this event.
	 */
	public static final Event<AllowClimb> ALLOW_CLIMB = EventFactory.createArrayBacked(AllowClimb.class, callbacks -> ((entity, pos, state) -> {
		for (AllowClimb callback : callbacks) {
			if (callback.allowClimb(entity, pos, state)) {
				return true;
			}
		}

		return false;
	}));

	/**
	 * An event that is called when minecraft checks if an entity is at a climbable block position.
	 * This is fired after minecraft checks if the block is in {@link net.minecraft.registry.tag.BlockTags#CLIMBABLE}
	 *
	 * <p>This event is for adding extra conditions to minecraft's climbing behaviour,
	 * and if any callback returns {@code false}, position will be declared unclimbable.
	 */
	public static final Event<AllowClimbClimbable> ALLOW_CLIMB_CLIMBABLE = EventFactory.createArrayBacked(AllowClimbClimbable.class, callbacks -> (entity, pos, state) -> {
		for (AllowClimbClimbable callback : callbacks) {
			if (!callback.allowClimb(entity, pos, state)) {
				return false;
			}
		}

		return true;
	});

	@FunctionalInterface
	public interface AllowDamage {
		/**
		 * Called when a living entity is going to take damage. Can be used to cancel the damage entirely.
		 *
		 * <p>The amount corresponds to the "incoming" damage amount, before armor and other mitigations have been applied.
		 *
		 * @param entity the entity
		 * @param source the source of the damage
		 * @param amount the amount of damage that the entity will take (before mitigations)
		 * @return true if the damage should go ahead, false to cancel the damage.
		 */
		boolean allowDamage(LivingEntity entity, DamageSource source, float amount);
	}

	@FunctionalInterface
	public interface AllowDeath {
		/**
		 * Called when a living entity takes fatal damage (before totems of undying can take effect).
		 *
		 * @param entity       the entity
		 * @param damageSource the source of the fatal damage
		 * @param damageAmount the amount of damage that has killed the entity
		 * @return true if the death should go ahead, false to cancel the death.
		 */
		boolean allowDeath(LivingEntity entity, DamageSource damageSource, float damageAmount);
	}

	@FunctionalInterface
	public interface AfterDeath {
		/**
		 * Called when a living entity dies. The death cannot be canceled at this point.
		 *
		 * @param entity       the entity
		 * @param damageSource the source of the fatal damage
		 */
		void afterDeath(LivingEntity entity, DamageSource damageSource);
	}

	@FunctionalInterface
	public interface AllowClimb {
		/**
		 * Called when minecraft checks if a living entity is in a climbable block pos.
		 *
		 * @param entity the entity
		 * @param pos    the block pos being checked
		 * @param state  the block state of the block at {@code pos}
		 */
		boolean allowClimb(LivingEntity entity, BlockPos pos, BlockState state);
	}

	public interface AllowClimbClimbable {
		/**
		 * Called after minecraft checks if a living entity is in a climbable block pos.
		 * If your block is in {@link net.minecraft.registry.tag.BlockTags#CLIMBABLE},
		 * you can check for extra conditions here.
		 *
		 * @param entity the entity
		 * @param pos    the block pos being checked
		 * @param state  the block state of the block at {@code pos}
		 * @return {@link ActionResult#SUCCESS} to allow climbing,
		 * {@link ActionResult#FAIL} to prevent climbing,
		 * {@link ActionResult#PASS} to fall back to other callbacks or vanilla
		 */
		boolean allowClimb(LivingEntity entity, BlockPos pos, BlockState state);
	}

	private ServerLivingEntityEvents() {
	}
}
