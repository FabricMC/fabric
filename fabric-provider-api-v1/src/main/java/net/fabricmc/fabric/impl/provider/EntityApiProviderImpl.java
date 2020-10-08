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
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.provider.v1.ApiProvider;
import net.fabricmc.fabric.api.provider.v1.EntityApiProvider;

public final class EntityApiProviderImpl<A extends ApiProvider<A>> extends AbstractApiProvider<A> implements EntityApiProvider<A> {
	private final Reference2ReferenceOpenHashMap<EntityType<?>, Function<Entity, A>> map = new Reference2ReferenceOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);

	EntityApiProviderImpl(Class<A> apiType, A absentApi) {
		super(apiType, absentApi);
		map.defaultReturnValue(e -> absentApi);
	}

	@Override
	public void registerProviderForEntity(Function<Entity, A> mapping, EntityType<?> entityType) {
		Objects.requireNonNull(mapping, "encountered API provider mapping");
		Objects.requireNonNull(entityType, "encountered null entity type in API provider mapping");

		if (map.putIfAbsent(entityType, mapping) != null) {
			LOGGER.warn("Encountered duplicate API Provider registration for entity " + Registry.ENTITY_TYPE.getId(entityType));
		}
	}

	@Override
	public A getApiFromEntity(Entity entity) {
		return map.get(entity.getType()).apply(entity);
	}

	private static final ApiProviderRegistry<EntityApiProvider<?>> REGISTRY = new ApiProviderRegistry<>();

	public static <A extends ApiProvider<A>> EntityApiProvider<A> registerProvider(Identifier id, Class<A> type, A absentApi) {
		final EntityApiProvider<A> result = new EntityApiProviderImpl<> (type, absentApi);
		REGISTRY.register(id, result);
		return result;
	}

	@Nullable
	public static EntityApiProvider<?> getProvider(Identifier id) {
		return REGISTRY.get(id);
	}
}
