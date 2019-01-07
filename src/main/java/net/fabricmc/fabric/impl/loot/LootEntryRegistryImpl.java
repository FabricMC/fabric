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

package net.fabricmc.fabric.impl.loot;

import net.fabricmc.fabric.api.loot.LootEntryRegistry;
import net.minecraft.world.loot.entry.LootEntries;
import net.minecraft.world.loot.entry.LootEntry;

import java.util.function.Consumer;

public final class LootEntryRegistryImpl implements LootEntryRegistry {
	private static Consumer<LootEntry.Serializer<?>> registerFunction;
	public static final LootEntryRegistryImpl INSTANCE = new LootEntryRegistryImpl();

	private LootEntryRegistryImpl() {}

	@Override
	public void register(LootEntry.Serializer<?> serializer) {
		loadLootEntries();
		registerFunction.accept(serializer);
	}

	public static void setRegisterFunction(Consumer<LootEntry.Serializer<?>> registerFunction) {
		LootEntryRegistryImpl.registerFunction = registerFunction;
	}

	private static void loadLootEntries() {
		try { Class.forName(LootEntries.class.getCanonicalName()); } catch (ClassNotFoundException e) {}
	}
}
