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

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;

/**
 * An interface to implement on all custom crossbows.
 *
 */
public interface FabricCrossbowHooks {
	/**
	 * Allows editing of the shot arrow from the crossbow. All default crossbow
	 * properties are applied first.
	 *
	 * @param arrowItem                  The arrow type
	 * @param persistentProjectileEntity The arrow entity
	 */
	void createArrow(ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity);

	/**
	 * Gets the speed of the crossbow projectile.
	 *
	 * @return The speed
	 */
	float getSpeed();
}
