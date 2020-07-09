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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FabricItemUpdateAnimationHandlers {
	private static final Map<Item, UpdateAnimationHandler> handlers = new HashMap<>();
	private static final UpdateAnimationHandler DEFAULT = (original, updated) -> true;

	public static void register(Item item, UpdateAnimationHandler handler) {
		if (handlers.containsKey(item)) {
			Identifier registryID = Registry.ITEM.getId(item);
			throw new UnsupportedOperationException(String.format("Attempted to register an Item Update Animation Handler for %s, but one was already registered!", registryID.toString()));
		} else {
			handlers.put(item, handler);
		}
	}

	public static UpdateAnimationHandler get(Item item) {
		return handlers.getOrDefault(item, DEFAULT);
	}
}
