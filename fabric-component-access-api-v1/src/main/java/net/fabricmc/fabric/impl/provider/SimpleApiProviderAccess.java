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

import java.util.function.Function;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import net.fabricmc.fabric.api.provider.v1.ApiProvider;
import net.fabricmc.fabric.api.provider.v1.ApiProviderAccess;

abstract class SimpleApiProviderAccess<P extends ApiProvider<P, A>, A, K, E> extends AbstractApiProviderAccess<P, A> implements ApiProviderAccess<P, A> {
	protected final Reference2ReferenceOpenHashMap<K, Function<E, P>> map = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);

	SimpleApiProviderAccess(Class<A> apiType, P absentProvider) {
		super(apiType, absentProvider);
		map.defaultReturnValue(e -> absentProvider);
	}
}
