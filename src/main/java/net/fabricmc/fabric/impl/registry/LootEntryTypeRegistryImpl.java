/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.registry;

import net.fabricmc.fabric.api.registry.LootEntryTypeRegistry;
import net.minecraft.world.loot.entry.LootEntries;
import net.minecraft.world.loot.entry.LootEntry;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public final class LootEntryTypeRegistryImpl implements LootEntryTypeRegistry {
	private static Consumer<LootEntry.Serializer<?>> registerFunction;
	public static final LootEntryTypeRegistryImpl INSTANCE = new LootEntryTypeRegistryImpl();
	private static final Method REGISTER_METHOD;

	static {
		Method target = null;
		for (Method m : LootEntries.class.getDeclaredMethods()) {
			if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == LootEntry.Serializer.class) {
				if (target != null) {
					throw new RuntimeException("More than one register-like method found in LootEntries!");
				} else {
					target = m;
				}
			}
		}

		if (target == null) {
			throw new RuntimeException("Could not find register-like method in LootEntries!");
		} else {
			REGISTER_METHOD = target;
			REGISTER_METHOD.setAccessible(true);
		}
	}

	private LootEntryTypeRegistryImpl() {}

	@Override
	public void register(LootEntry.Serializer<?> serializer) {
		try {
			REGISTER_METHOD.invoke(null, serializer);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
