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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events related to {@link LivingEntity living entities}.
 */
public final class LivingEntityEvents {
	/**
	 * Called after a living entity has taken damage and the entity's new health has been set.
	 *
	 * <p>This is called before any {@link Items#TOTEM_OF_UNDYING} are applied.
	 *
	 * <p>Note this method does not work on {@link ArmorStandEntity armor stands} right now.
	 */
	public static final Event<AfterDamaged> AFTER_DAMAGED = EventFactory.createArrayBacked(AfterDamaged.class, callbacks -> (entity, damageSource, damageAmount, originalHeath) -> {
		for (AfterDamaged callback : callbacks) {
			callback.afterDamaged(entity, damageSource, damageAmount, originalHeath);
		}
	});

	/**
	 * Called after a living entity is killed by an adversary.
	 */
	public static final Event<AfterKilledByAdversary> AFTER_KILLED_BY_ADVERSARY = EventFactory.createArrayBacked(AfterKilledByAdversary.class, callbacks -> (deadEntity, adversary) -> {
		for (AfterKilledByAdversary callback : callbacks) {
			callback.afterKilledBy(deadEntity, adversary);
		}
	});

	public interface AfterDamaged {
		/**
		 * Called after a living entity is damaged.
		 *
		 * @param entity the entity being damaged
		 * @param damageSource the damage source
		 * @param damageAmount the amount of damage
		 * @param originalHeath the original health of the entity
		 */
		void afterDamaged(LivingEntity entity, DamageSource damageSource, float damageAmount, float originalHeath);
	}

	public interface AfterKilledByAdversary {
		/**
		 * Called when a living entity is killed by an adversary.
		 *
		 * @param deadEntity the entity which has been killed
		 * @param adversary the adversary
		 */
		void afterKilledBy(LivingEntity deadEntity, LivingEntity adversary);
	}

	private LivingEntityEvents() {
	}
}
