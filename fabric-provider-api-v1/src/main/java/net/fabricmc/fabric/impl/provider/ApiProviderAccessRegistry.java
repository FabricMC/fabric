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

package net.fabricmc.fabric.impl.provider;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.provider.v1.ApiProviderAccess;

public final class ApiProviderAccessRegistry<T extends ApiProviderAccess<?, ?>> {
	ApiProviderAccessRegistry () {
	}

	private final Object2ObjectOpenHashMap<Identifier, T> MAP = new Object2ObjectOpenHashMap<>();

	public void register(Identifier id, T access) {
		if (MAP.putIfAbsent(id, access) != null) {
			AbstractApiProviderAccess.LOGGER.info("Encountered duplicate API Provider access registeration with ID " + id.toString());
		}
	}

	/* @Nullabe */
	public T get(Identifier id) {
		return MAP.get(id);
	}
}
