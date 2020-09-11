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

import java.util.Objects;
import java.util.function.Function;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.provider.v1.ApiProvider;
import net.fabricmc.fabric.api.provider.v1.EntityApiProviderAccess;

public final class EntityApiProviderAccessImpl<P extends ApiProvider<P, A>, A> extends AbstractApiProviderAccess<P, A> implements EntityApiProviderAccess<P, A> {
	private final Reference2ReferenceOpenHashMap<EntityType<?>, Function<Entity, P>> map = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);

	EntityApiProviderAccessImpl(Class<A> apiType, P absentProvider) {
		super(apiType, absentProvider);
		map.defaultReturnValue(e -> absentProvider);
	}

	@Override
	public void registerProviderForEntity(Function<Entity, P> mapping, EntityType<?> entityType) {
		Objects.requireNonNull(mapping, "encountered API provider mapping");
		Objects.requireNonNull(entityType, "encountered null entity type in API provider mapping");

		if (map.putIfAbsent(entityType, mapping) != null) {
			LOGGER.warn("Encountered duplicate API Provider registration for entity " + Registry.ENTITY_TYPE.getId(entityType));
		}
	}

	@Override
	public P getProviderFromEntity(Entity entity) {
		return map.get(entity.getType()).apply(entity);
	}

	private static final ApiProviderAccessRegistry<EntityApiProviderAccess<?, ?>> REGISTRY = new ApiProviderAccessRegistry<>();

	public static <P extends ApiProvider<P, A>, A> EntityApiProviderAccess<P, A> registerAccess(Identifier id, Class<A> type, P absentProvider) {
		final EntityApiProviderAccess<P, A> result = new EntityApiProviderAccessImpl<> (type, absentProvider);
		REGISTRY.register(id, result);
		return result;
	}

	public static EntityApiProviderAccess<?, ?> getAccess(Identifier id) {
		return REGISTRY.get(id);
	}
}
