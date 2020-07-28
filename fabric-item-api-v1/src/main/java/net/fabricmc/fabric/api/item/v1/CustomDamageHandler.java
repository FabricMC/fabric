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

import java.util.function.Consumer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * Implement this interface on an item to run custom logic when {@link ItemStack#damage(int, LivingEntity, Consumer)} is called.
 */
public interface CustomDamageHandler {
	/**
	 * Called to apply damage to the given stack.
	 * This can be used to e.g. drain from a battery before actually damaging the item
	 * @param amount The amount of damage originally requested
	 * @return The amount of damage to pass to vanilla's logic
	 */
	<T extends LivingEntity> int damage(ItemStack stack, int amount, T entity, Consumer<T> breakCallback);
}
