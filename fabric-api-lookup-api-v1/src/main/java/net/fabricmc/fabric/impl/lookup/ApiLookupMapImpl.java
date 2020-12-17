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

package net.fabricmc.fabric.impl.lookup;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.lookup.v1.ApiLookupMap;

public final class ApiLookupMapImpl<L> implements ApiLookupMap<L> {
	private final Map<Identifier, StoredLookup<L>> lookups = new Reference2ReferenceOpenHashMap<>();
	private final Supplier<L> lookupFactory;

	public ApiLookupMapImpl(Supplier<L> lookupFactory) {
		this.lookupFactory = lookupFactory;
	}

	@Override
	public synchronized L getLookup(Identifier lookupId, Class<?> apiClass, Class<?> contextClass) {
		StoredLookup<L> storedLookup = lookups.computeIfAbsent(lookupId, id -> new StoredLookup<>(lookupFactory.get(), apiClass, contextClass));

		if (storedLookup.apiClass != apiClass || storedLookup.contextClass != contextClass) {
			throw new IllegalArgumentException(String.format(
					"Lookup with id %s is already registered with api class %s and context class %s. It can't be registered with api class %s and context class %s",
					lookupId, storedLookup.apiClass.getCanonicalName(), storedLookup.contextClass.getCanonicalName(), apiClass.getCanonicalName(), contextClass.getCanonicalName()
					));
		} else {
			return storedLookup.lookup;
		}
	}

	@Override
	public synchronized Iterator<L> iterator() {
		return lookups.values().stream().map(storedLookup -> storedLookup.lookup).collect(Collectors.toList()).iterator();
	}

	private static class StoredLookup<L> {
		private final L lookup;
		private final Class<?> apiClass;
		private final Class<?> contextClass;

		private StoredLookup(L lookup, Class<?> apiClass, Class<?> contextClass) {
			this.lookup = lookup;
			this.apiClass = apiClass;
			this.contextClass = contextClass;
		}
	}
}
