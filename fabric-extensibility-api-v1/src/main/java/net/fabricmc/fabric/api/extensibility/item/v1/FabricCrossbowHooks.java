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

package net.fabricmc.fabric.api.extensibility.item.v1;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * An interface to implement on all custom crossbows. <br>
 * Note: This is meant to be used on a CrossbowItem class
 */
public interface FabricCrossbowHooks {
	/**
	 * Allows editing of the shot arrow from the crossbow. Applies all crossbow
	 * projectile properties first.
	 *
	 * @param arrowItem                  The arrow type
	 * @param persistentProjectileEntity The arrow entity
	 */
	void createArrow(ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity);

	/**
	 * Gets the speed of the crossbow projectile. <br>
	 * To get the projectile from the crossbow, call {@link CrossbowItem#hasProjectile(ItemStack, Item)} passing in {@code stack} and the {@link Item} for the projectile
	 *
	 *
	 * @param stack The ItemStack for the crossbow
	 * @param entity The entity shooting the crossbow
	 * @return The speed of the projectile
	 */
	float getSpeed(ItemStack stack, LivingEntity entity);
}
