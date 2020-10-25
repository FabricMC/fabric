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

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.provider.v1.ApiLookup;
import net.fabricmc.fabric.api.provider.v1.ApiLookupMap;
import net.fabricmc.fabric.api.provider.v1.ContextKey;

public final class ApiLookupMapImpl<L extends ApiLookup<?>> implements ApiLookupMap<L> {
	private final Map<Identifier, Map<ContextKey<?>, L>> lookups = new Reference2ReferenceOpenHashMap<>();
	private final LookupFactory<L> lookupFactory;

	public ApiLookupMapImpl(LookupFactory<L> lookupFactory) {
		this.lookupFactory = lookupFactory;
	}

	@Override
	public synchronized L getLookup(Identifier key, ContextKey<?> contextKey) {
		lookups.putIfAbsent(key, new Reference2ReferenceOpenHashMap<>());
		lookups.get(key).computeIfAbsent(contextKey, ctx -> lookupFactory.create(key, contextKey));
		return lookups.get(key).get(contextKey);
	}

	@Override
	public synchronized Iterator<L> iterator() {
		return lookups.values().stream().flatMap(apiLookups -> apiLookups.values().stream()).collect(Collectors.toList()).iterator();
	}
}
