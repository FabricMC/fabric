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

package net.fabricmc.fabric.impl.lookup.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;

public class EntityApiLookupImpl<A, C> implements EntityApiLookup<A, C> {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-api-lookup-api-v1/entity");
	private static final ApiLookupMap<EntityApiLookup<?, ?>> LOOKUPS = ApiLookupMap.<EntityApiLookup<?, ?>>create(EntityApiLookupImpl::new);
	private static final Map<Class<?>, Set<EntityType<?>>> REGISTERED_SELVES = new HashMap<>();
	private static boolean checkEntityLookup = true;

	private final Identifier identifier;
	private final Class<A> apiClass;
	private final Class<C> contextClass;
	private final ApiProviderMap<EntityType<?>, EntityApiProvider<A, C>> providerMap = ApiProviderMap.create();
	private final List<EntityApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();

	private EntityApiLookupImpl(Identifier identifier, Class<A> apiClass, Class<C> contextClass) {
		this.identifier = identifier;
		this.apiClass = apiClass;
		this.contextClass = contextClass;
	}

	@SuppressWarnings("unchecked")
	public static <A, C> EntityApiLookup<A, C> get(Identifier lookupId, Class<A> apiClass, Class<C> contextClass) {
		return (EntityApiLookup<A, C>) LOOKUPS.getLookup(lookupId, apiClass, contextClass);
	}

	public static void checkSelfImplementingTypes(MinecraftServer server) {
		if (checkEntityLookup) {
			checkEntityLookup = false;

			synchronized (REGISTERED_SELVES) {
				REGISTERED_SELVES.forEach((apiClass, entityTypes) -> {
					for (EntityType<?> entityType : entityTypes) {
						Entity entity = entityType.create(server.getOverworld());

						if (entity == null) {
							String errorMessage = String.format(
									"Failed to register self-implementing entities for API class %s. Can not create entity of type %s.",
									apiClass.getCanonicalName(),
									Registry.ENTITY_TYPE.getId(entityType)
							);
							throw new NullPointerException(errorMessage);
						}

						if (!apiClass.isInstance(entity)) {
							String errorMessage = String.format(
									"Failed to register self-implementing entities. API class %s is not assignable from entity class %s.",
									apiClass.getCanonicalName(),
									entity.getClass().getCanonicalName()
							);
							throw new IllegalArgumentException(errorMessage);
						}
					}
				});
			}
		}
	}

	@Override
	@Nullable
	public A find(Entity entity, C context) {
		Objects.requireNonNull(entity, "Entity may not be null.");

		if (EntityPredicates.VALID_ENTITY.test(entity)) {
			EntityApiProvider<A, C> provider = providerMap.get(entity.getType());

			if (provider != null) {
				A instance = provider.find(entity, context);

				if (instance != null) {
					return instance;
				}
			}

			for (EntityApiProvider<A, C> fallback : fallbackProviders) {
				A instance = fallback.find(entity, context);

				if (instance != null) {
					return instance;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerSelf(EntityType<?>... entityTypes) {
		synchronized (REGISTERED_SELVES) {
			REGISTERED_SELVES.computeIfAbsent(apiClass, c -> new LinkedHashSet<>()).addAll(Arrays.asList(entityTypes));
		}

		registerForTypes((entity, context) -> (A) entity, entityTypes);
	}

	@Override
	public void registerForTypes(EntityApiProvider<A, C> provider, EntityType<?>... entityTypes) {
		Objects.requireNonNull(provider, "EntityApiProvider may not be null.");

		if (entityTypes.length == 0) {
			throw new IllegalArgumentException("Must register at least one EntityType instance with an EntityApiProvider.");
		}

		for (EntityType<?> entityType : entityTypes) {
			if (providerMap.putIfAbsent(entityType, provider) != null) {
				LOGGER.warn("Encountered duplicate API provider registration for entity type: " + Registry.ENTITY_TYPE.getId(entityType));
			}
		}
	}

	@Override
	public void registerFallback(EntityApiProvider<A, C> fallbackProvider) {
		Objects.requireNonNull(fallbackProvider, "EntityApiProvider may not be null.");

		fallbackProviders.add(fallbackProvider);
	}

	@Override
	public Identifier getId() {
		return identifier;
	}

	@Override
	public Class<A> apiClass() {
		return apiClass;
	}

	@Override
	public Class<C> contextClass() {
		return contextClass;
	}

	@Override
	@Nullable
	public EntityApiProvider<A, C> getProvider(EntityType<?> entityType) {
		return providerMap.get(entityType);
	}
}
