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

import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents an {@link net.minecraft.item.Item} that decides whether to run its NBT update animation.
 */
@Environment(EnvType.CLIENT)
public interface UpdateAnimationHandler {
	/**
	 * Called once per tick to check whether the given held {@link ItemStack} should run the update animation.
	 *
	 * <p>In vanilla, the update animation is run if an {@link ItemStack} has any changed NBT values.
	 * The second {@link ItemStack} parameter is the most up-to-date version, while the original {@link ItemStack} is a cached version from the previous tick.
	 * Both {@link ItemStack} instances are guaranteed to have the same underlying {@link net.minecraft.item.Item}.
	 *
	 * @param original  cached {@link ItemStack} from previous tick
	 * @param updated  updated {@link ItemStack} to check for updates on
	 * @return  whether or not the item update animation should play
	 */
	boolean shouldRunAnimationUpdate(ItemStack original, ItemStack updated);
}
