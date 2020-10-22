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

package net.fabricmc.fabric.api.extensibility.item.v1.bow;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;

/**
 * An interface to implement for all custom bows in fabric. <br>
 * Note: This is meant to be used on a BowItem class, the functionality will not work otherwise.
 */
public interface BowInterface {
	/**
	 * In this method you can modify the behavior of arrows shot from your custom bow. Applies all of the vanilla arrow modifiers first.
	 *
	 * @param bowStack                   The ItemStack for the Bow Item
	 * @param arrowStack                 The ItemStack for the arrows
	 * @param user                       The user of the bow
	 * @param remainingUseTicks          The ticks remaining on the bow usage
	 * @param persistentProjectileEntity The arrow entity to be spawned
	 */
	void modifyShotProjectile(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, int remainingUseTicks, PersistentProjectileEntity persistentProjectileEntity);
}
