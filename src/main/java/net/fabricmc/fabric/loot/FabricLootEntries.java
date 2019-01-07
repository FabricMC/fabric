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

package net.fabricmc.fabric.loot;

import net.minecraft.world.loot.entry.LootEntries;
import net.minecraft.world.loot.entry.LootEntry;

import java.util.function.Consumer;

/**
 * Fabric's extensions to {@code net.minecraft.world.loot.entry.LootEntries}.
 *
 * @see #register
 */
public final class FabricLootEntries {
	private static Consumer<LootEntry.Serializer<?>> registerFunction;

	/**
	 * Registers loot entry types.
	 *
	 * @param serializer the loot entry serializer
	 */
	public static void register(LootEntry.Serializer<?> serializer) {
		loadLootEntries();
		registerFunction.accept(serializer);
	}

	// INTERNALS

	/**
	 * INTERNAL!
	 *
	 * @param registerFunction
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static void setRegisterFunction(Consumer<LootEntry.Serializer<?>> registerFunction) {
		FabricLootEntries.registerFunction = registerFunction;
	}

	private static void loadLootEntries() {
		try { Class.forName(LootEntries.class.getCanonicalName()); } catch (ClassNotFoundException e) {}
	}
}
