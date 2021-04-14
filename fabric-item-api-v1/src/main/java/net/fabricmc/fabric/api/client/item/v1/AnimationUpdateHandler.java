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

package net.fabricmc.fabric.api.client.item.v1;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.ItemUpdateAnimationHandlerExtensions;

/**
 * Provides information on whether an {@link net.minecraft.item.Item} should run its update animation, such as for NBT updates.
 */
@Environment(EnvType.CLIENT)
public interface AnimationUpdateHandler {
	/**
	 * Called once per tick to check whether the given held {@link ItemStack} should run the update animation.
	 *
	 * <p>In vanilla, the update animation is run if an {@link ItemStack} is not {@linkplain ItemStack#areEqual
	 * equal} to its previous version, such as any NBT value change.
	 * The {@code updated} is the most up-to-date version, while the {@code original} is a cached version from the previous tick.
	 * Both {@link ItemStack} instances are guaranteed to have the same {@linkplain ItemStack#getItem() underlying item}.
	 *
	 * @param original cached {@link ItemStack} from previous tick
	 * @param updated  updated {@link ItemStack} to check for updates on
	 * @return {@code true} if the item update animation should play, {@code false} otherwise
	 */
	@Environment(EnvType.CLIENT)
	boolean updateAnimation(ItemStack original, ItemStack updated);

	/**
	 * Registers an {@link AnimationUpdateHandler} for the given {@link ItemConvertible}.
	 *
	 * <p>Note that each Item can only have 1 update animation handler associated with it.
	 * If more than 1 handler is registered, an {@link UnsupportedOperationException} is thrown.
	 *
	 * @param item    {@link ItemConvertible} to define update behavior for
	 * @param handler update behavior of the given item
	 * @throws NullPointerException          if null is passed in, or {@link ItemConvertible#asItem()} is null.
	 * @throws UnsupportedOperationException if the given {@link ItemConvertible} already has an update handler
	 */
	static void register(ItemConvertible item, AnimationUpdateHandler handler) {
		Objects.requireNonNull(item, "Attempted to retrieve an Update Animation Handler for an invalid item!");
		Objects.requireNonNull(item.asItem(), "Attempted to retrieve an Update Animation Handler for an invalid item!");

		if (((ItemUpdateAnimationHandlerExtensions) item.asItem()).fabric_getUpdateAnimationHandler() != null) {
			Identifier registryID = Registry.ITEM.getId(item.asItem());
			throw new UnsupportedOperationException(String.format("Attempted to register an Item Update Animation Handler for %s, but one was already registered!", registryID.toString()));
		} else {
			((ItemUpdateAnimationHandlerExtensions) item.asItem()).fabric_setUpdateAnimationHandler(handler);
		}
	}

	/**
	 * Returns the {@link AnimationUpdateHandler} associated with the given {@link ItemConvertible}.
	 *
	 * <p>If no UpdateAnimationHandler has been assigned to the ItemConvertible, null is returned.
	 *
	 * @param item item to retrieve an {@link AnimationUpdateHandler} for
	 * @return the {@link AnimationUpdateHandler} associated with the given {@link ItemConvertible}, or null if one has not been assigned
	 * @throws NullPointerException if null is passed in, or {@link ItemConvertible#asItem()} is null.
	 */
	@Nullable
	static AnimationUpdateHandler get(ItemConvertible item) {
		Objects.requireNonNull(item, "Attempted to retrieve an Update Animation Handler for an invalid item!");
		Objects.requireNonNull(item.asItem(), "Attempted to retrieve an Update Animation Handler for an invalid item!");
		return ((ItemUpdateAnimationHandlerExtensions) item.asItem()).fabric_getUpdateAnimationHandler();
	}
}
