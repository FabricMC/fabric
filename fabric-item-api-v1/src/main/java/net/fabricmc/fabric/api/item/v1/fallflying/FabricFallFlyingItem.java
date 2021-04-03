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

package net.fabricmc.fabric.api.item.v1.fallflying;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

/**
 * An interface to implement on all custom fall flying items (which allow flying like elytras).
 */
public interface FabricFallFlyingItem {
	/**
	 * Returns if the fall flying item is usable based on its stack and user.
	 *
	 * @param stack the stack for the fall flying item
	 * @param user  the user of the fall flying
	 * @return {@code true} if the fall flying is usable
	 */
	default boolean shouldAllowFallFlying(ItemStack stack, LivingEntity user) {
		return ElytraItem.isUsable(stack);
	}
}
