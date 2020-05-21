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

package net.fabricmc.fabric.impl.loot;

import net.minecraft.loot.entry.LootEntry;

import net.fabricmc.fabric.api.loot.v2.LootEntryTypeRegistry;
import net.fabricmc.fabric.mixin.loot.LootEntriesAccessor;

public final class LootEntryTypeRegistryImpl implements LootEntryTypeRegistry {
	public static final LootEntryTypeRegistryImpl INSTANCE = new LootEntryTypeRegistryImpl();

	private LootEntryTypeRegistryImpl() { }

	@Override
	public void register(LootEntry.Serializer<?> serializer) {
		LootEntriesAccessor.callRegister(serializer);
	}
}
