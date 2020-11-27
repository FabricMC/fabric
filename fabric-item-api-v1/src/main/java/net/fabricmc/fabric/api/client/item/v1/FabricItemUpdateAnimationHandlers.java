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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.ItemUpdateAnimationHandlerExtensions;

@Environment(EnvType.CLIENT)
public final class FabricItemUpdateAnimationHandlers {
	public static void register(ItemConvertible item, UpdateAnimationHandler handler) {
		Objects.requireNonNull(item, "Attempted to retrieve an Update Animation Handler for an invalid item!");
		Objects.requireNonNull(item.asItem(), "Attempted to retrieve an Update Animation Handler for an invalid item!");

		if (((ItemUpdateAnimationHandlerExtensions) item.asItem()).fabric_getUpdateAnimationHandler() != null) {
			Identifier registryID = Registry.ITEM.getId(item.asItem());
			throw new UnsupportedOperationException(String.format("Attempted to register an Item Update Animation Handler for %s, but one was already registered!", registryID.toString()));
		} else {
			((ItemUpdateAnimationHandlerExtensions) item.asItem()).fabric_setUpdateAnimationHandler(handler);
		}
	}

	@Nullable
	public static UpdateAnimationHandler get(ItemConvertible item) {
		Objects.requireNonNull(item, "Attempted to retrieve an Update Animation Handler for an invalid item!");
		Objects.requireNonNull(item.asItem(), "Attempted to retrieve an Update Animation Handler for an invalid item!");
		return ((ItemUpdateAnimationHandlerExtensions) item.asItem()).fabric_getUpdateAnimationHandler();
	}

	private FabricItemUpdateAnimationHandlers() {
	}
}
