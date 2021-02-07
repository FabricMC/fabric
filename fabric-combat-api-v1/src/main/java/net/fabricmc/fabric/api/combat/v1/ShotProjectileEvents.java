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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ShotProjectileEvents {
	private ShotProjectileEvents() {
	}

	public static final Event<ProjectileFromBow> BOW_SHOT_PROJECTILE = EventFactory.createArrayBacked(ProjectileFromBow.class, callbacks -> (ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, PersistentProjectileEntity persistentProjectileEntity) -> {
		for (ProjectileFromBow callback : callbacks) {
			persistentProjectileEntity = callback.onProjectileShot(bowStack, arrowStack, user, pullProgress, persistentProjectileEntity);
		}

		return persistentProjectileEntity;
	});

	public interface ProjectileFromBow {
		/**
		 * In this method you can modify the behavior of arrows shot from your custom bow. Applies all of the vanilla arrow modifiers first.
		 *
		 * @param bowStack                   The ItemStack for the Bow Item
		 * @param arrowStack                 The ItemStack for the arrows
		 * @param user                       The user of the bow
		 * @param pullProgress               The pull progress of the bow from 0.0 to 1.0
		 * @param persistentProjectileEntity The arrow entity to be spawned
		 * @return The arrow entity, either new or modified
		 */
		PersistentProjectileEntity onProjectileShot(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, float pullProgress, PersistentProjectileEntity persistentProjectileEntity);
	}
}
