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

package net.fabricmc.fabric.impl.tool.attribute;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.LivingEntity;

/**
 * Allows us to keep track of the current user of a dynamic tool when vanilla does not pass the player explicitly,
 * by setting and unsetting the current player wherever it is possible in the call stack.
 */
public class DynamicToolContext {
	private static final ThreadLocal<LivingEntity> CURRENT_USER = new ThreadLocal<>();

	@Nullable
	public static LivingEntity get() {
		return CURRENT_USER.get();
	}

	public static void set(Object entity) { // Allows object to avoid a cast in each mixin.
		if (entity instanceof LivingEntity livingEntity) {
			CURRENT_USER.set(livingEntity);
		} else {
			throw new IllegalArgumentException("Expected living entity dynamic tool context.");
		}
	}

	public static void clear() {
		CURRENT_USER.remove();
	}
}
