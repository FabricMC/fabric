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
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

/**
 * An interface that allows implementing items to enable elytra flight for living entities, when equipped in the chest equipment slot.
 * While being used, the item will be damaged <b>every 20 ticks</b> through {@link ItemStack#damage(int, LivingEntity, Consumer)}.
 */
public interface FabricElytraFlyingItem {
	/**
	 * Returns {@code true} if this item allows flight based on its stack and user, and {@code false} otherwise.
	 * This function will be called when an entity attempts to start elytra flight,
	 * and will also be called every tick during elytra flight to make sure it is still allowed.
	 *
	 * <p>It may be called multiple times in a row in some cases, so you should not modify the stack directly,
	 * but rather check every tick if your item is equipped and {@linkplain LivingEntity#isFallFlying() the entity is fall flying}.
	 *
	 * @param stack the stack for the elytra like item
	 * @param user  the user of the elytra like
	 * @return {@code true} if the elytra like is usable
	 */
	default boolean shouldAllowElytraFlight(ItemStack stack, LivingEntity user) {
		return ElytraItem.isUsable(stack);
	}
}
