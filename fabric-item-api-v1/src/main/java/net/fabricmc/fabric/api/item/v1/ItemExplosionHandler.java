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

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;

/**
 * Allows custom logic to dictate whether an item may be destroyed in an explosion.
 *
 * <p>Item explosion handlers may be set with {@link FabricItemSettings#explosionHandler(ItemExplosionHandler)}.
 *
 * Note that a {@link Items#NETHER_STAR} is always immune to explosions and cannot be set to be vulnerable to explosions.
 */
@FunctionalInterface
public interface ItemExplosionHandler {
	/**
	 * Checks if an item should not be destroyed in an explosion.
	 *
	 * @param entity the item entity, may be used to get the item stack
	 * @param source the damage source
	 * @param amount the amount of damage being applied to the item entity
	 * @return whether the item should not be destroyed in the explosion.
	 */
	boolean shouldNotDestroy(ItemEntity entity, DamageSource source, float amount);
}
