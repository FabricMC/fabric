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

package net.fabricmc.fabric.api.combat.v1;

import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.combat.v1.bow.FabricBowItem;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ShotProjectileEvents {
	private ShotProjectileEvents() {
	}

	/**
	 * This event modifies the projectile entity. Returning a new entity will not change the entity that is spawned in.
	 */
	public static final Event<ModifyProjectileFromBow> BOW_MODIFY_SHOT_PROJECTILE = EventFactory.createArrayBacked(ModifyProjectileFromBow.class, callbacks -> (bowStack, arrowStack, user, pullProgress, persistentProjectileEntity) -> {
		for (ModifyProjectileFromBow callback : callbacks) {
			if (callback instanceof FabricBowItem) {
				if (callback == bowStack.getItem()) {
					callback.modifyProjectileShot(bowStack, arrowStack, user, pullProgress, persistentProjectileEntity);
				}
			} else {
				callback.modifyProjectileShot(bowStack, arrowStack, user, pullProgress, persistentProjectileEntity);
			}
		}
	});

	/**
	 * This event replaces the projectile entity. Any modifications done in this step without returning a new entity can be erased.
	 * Do not use this event to only modify the arrow entity, as {@link ShotProjectileEvents#BOW_MODIFY_SHOT_PROJECTILE} is the proper event.
	 */
	public static final Event<ReplaceProjectileFromBow> BOW_REPLACE_SHOT_PROJECTILE = EventFactory.createArrayBacked(ReplaceProjectileFromBow.class, callbacks -> (bowStack, arrowStack, user, pullProgress, persistentProjectileEntity) -> {
		for (ReplaceProjectileFromBow callback : callbacks) {
			PersistentProjectileEntity replacedEntity = callback.replaceProjectileShot(bowStack, arrowStack, user, pullProgress, persistentProjectileEntity);

			if (replacedEntity != null) {
				return replacedEntity;
			}
		}

		return persistentProjectileEntity;
	});

	public interface ReplaceProjectileFromBow {
		/**
		 * In this method you can replace the arrow shot from your custom bow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param bowStack                   The ItemStack for the Bow Item
		 * @param arrowStack                 The ItemStack for the arrows
		 * @param user                       The user of the bow
		 * @param pullProgress               The pull progress of the bow from 0.0 to 1.0
		 * @param persistentProjectileEntity The arrow entity to be spawned
		 * @return The arrow entity, either new or modified
		 */
		PersistentProjectileEntity replaceProjectileShot(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, @NotNull PersistentProjectileEntity persistentProjectileEntity);
	}

	public interface ModifyProjectileFromBow {
		/**
		 * In this method you can modify the behavior of arrows shot from your custom bow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param bowStack                   The ItemStack for the Bow Item
		 * @param arrowStack                 The ItemStack for the arrows
		 * @param user                       The user of the bow
		 * @param pullProgress               The pull progress of the bow from 0.0 to 1.0
		 * @param persistentProjectileEntity The arrow entity to be spawned
		 */
		void modifyProjectileShot(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, @NotNull PersistentProjectileEntity persistentProjectileEntity);
	}
}
