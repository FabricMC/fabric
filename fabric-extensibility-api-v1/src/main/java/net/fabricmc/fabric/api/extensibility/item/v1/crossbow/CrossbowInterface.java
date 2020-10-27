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

package net.fabricmc.fabric.api.extensibility.item.v1.crossbow;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * An interface to implement on all custom crossbows. <br>
 * Note: This is meant to be used on a CrossbowItem class, otherwise the functionality won't work
 *
 * @see SimpleCrossbowItem
 */
public interface CrossbowInterface {
	/**
	 * Allows editing of the projectile entity shot from the crossbow. Applies all crossbow
	 * projectile properties first.
	 *
	 * @param crossbowStack              The ItemStack for the crossbow
	 * @param entity                     The entity shooting the crossbow
	 * @param projectileStack            The stack for the projectile
	 * @param persistentProjectileEntity The projectile entity to be shot
	 */
	void modifyShotProjectile(ItemStack crossbowStack, LivingEntity entity, ItemStack projectileStack, PersistentProjectileEntity persistentProjectileEntity);

	/**
	 * Allows modifying the speed of the crossbow projectile. <br>
	 * To get the projectile from the crossbow, call {@link CrossbowItem#hasProjectile(ItemStack, Item)} passing in {@code stack} and the {@link Item} for the projectile
	 *
	 * @param stack  The ItemStack for the crossbow
	 * @param entity The Entity shooting the crossbow
	 * @return The speed of the projectile
	 */
	float getProjectileSpeed(ItemStack stack, LivingEntity entity);

	/**
	 * Returns the vanilla implementation for getSpeed, allowing for crossbows to act the same as the vanilla crossbow.
	 *
	 * @param stack The ItemStack for the crossbow
	 * @return The vanilla speed for the crossbow and its projectile
	 */
	default float getSpeed(ItemStack stack) {
		return stack.getItem() == Items.CROSSBOW && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
	}
}
