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

import net.fabricmc.fabric.api.provider.v1.ApiProvider;
import net.fabricmc.fabric.api.provider.v1.ApiProviderAccess;
import net.fabricmc.fabric.api.provider.v1.ApiProviderAccessRegistry;

public final class ApiProviderAccessRegistryImpl implements ApiProviderAccessRegistry {
	private ApiProviderAccessRegistryImpl() { }

	public static final ApiProviderAccessRegistry INSTANCE = new ApiProviderAccessRegistryImpl();

	private static final Object2ObjectOpenHashMap<Identifier, ApiProviderAccess<?, ?>> TYPES_BY_ID = new Object2ObjectOpenHashMap<>();

	@Override
	public <P extends ApiProvider<P, A>, A> ApiProviderAccess<P, A> createAccess(Identifier id, Class<A> type, P absentProvider) {
		final ApiProviderAccess<P, A> result = new ApiProviderAccessImpl<> (type, absentProvider);

		if (TYPES_BY_ID.putIfAbsent(id, result) != null) {
			throw new IllegalStateException("API Provider access already registered with ID " + id.toString());
		}

		return result;
	}

	@Override
	public ApiProviderAccess<?, ?> getAccess(Identifier id) {
		return TYPES_BY_ID.get(id);
	}
}
