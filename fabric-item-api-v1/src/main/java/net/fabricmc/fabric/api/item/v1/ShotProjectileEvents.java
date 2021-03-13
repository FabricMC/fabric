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

package net.fabricmc.fabric.api.item.v1;

import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ShotProjectileEvents {
	private ShotProjectileEvents() {
	}

	/**
	 * This event modifies the projectile entity shot from a crossbow.
	 */
	public static final Event<ModifyProjectileFromCrossbow> CROSSBOW_MODIFY_SHOT_PROJECTILE = EventFactory.createArrayBacked(ModifyProjectileFromCrossbow.class, callbacks -> (bowStack, projectileStack, user, persistentProjectileEntity) -> {
		for (ModifyProjectileFromCrossbow callback : callbacks) {
			callback.modifyProjectileShot(bowStack, projectileStack, user, persistentProjectileEntity);
		}
	});

	/**
	 * This event replaces the projectile entity shot from a crossbow. Any modifications done in this step without returning a new entity can be erased.
	 * Do not use this event to only modify the arrow entity, as {@link ShotProjectileEvents#CROSSBOW_MODIFY_SHOT_PROJECTILE} is the proper event.
	 */
	public static final Event<ReplaceProjectileFromCrossbow> CROSSBOW_REPLACE_SHOT_PROJECTILE = EventFactory.createArrayBacked(ReplaceProjectileFromCrossbow.class, callbacks -> (bowStack, projectileStack, user, persistentProjectileEntity) -> {
		for (ReplaceProjectileFromCrossbow callback : callbacks) {
			PersistentProjectileEntity replacedEntity = callback.replaceProjectileShot(bowStack, projectileStack, user, persistentProjectileEntity);

			if (replacedEntity != null) {
				return replacedEntity;
			}
		}

		return persistentProjectileEntity;
	});

	public interface ReplaceProjectileFromCrossbow {
		/**
		 * In this method you can replace the arrow shot from your custom crossbow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param crossbowStack              The ItemStack for the Crossbow Item
		 * @param projectileStack            The ItemStack for the projectile currently being shot
		 * @param user                       The user of the crossbow
		 * @param persistentProjectileEntity The arrow entity to be spawned
		 * @return The new projectile entity. Return null if you do not change the entity.
		 */
		PersistentProjectileEntity replaceProjectileShot(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity user, @NotNull PersistentProjectileEntity persistentProjectileEntity);
	}

	public interface ModifyProjectileFromCrossbow {
		/**
		 * In this method you can modify the behavior of arrows shot from your custom crossbow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param crossbowStack              The ItemStack for the Crossbow Item
		 * @param projectileStack            The ItemStack for the projectile currently being shot
		 * @param user                       The user of the crossbow
		 * @param persistentProjectileEntity The arrow entity to be spawned
		 */
		void modifyProjectileShot(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity user, @NotNull PersistentProjectileEntity persistentProjectileEntity);
	}
}
