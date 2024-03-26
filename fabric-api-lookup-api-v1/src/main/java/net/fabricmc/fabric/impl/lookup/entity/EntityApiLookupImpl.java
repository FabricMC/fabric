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
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;

public class EntityApiLookupImpl<A, C> implements EntityApiLookup<A, C> {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-api-lookup-api-v1/entity");
	private static final ApiLookupMap<EntityApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(EntityApiLookupImpl::new);
	private static final SetMultimap<Class<?>, EntityType<?>> REGISTERED_SELVES = MultimapBuilder.hashKeys().linkedHashSetValues().build();
	private static boolean checkEntityLookup = true;

	public static <A, C> Event<EntityApiProvider<A, C>> newEvent() {
		return EventFactory.createArrayBacked(EntityApiProvider.class, providers -> (entity, context) -> {
			for (EntityApiProvider<A, C> provider : providers) {
				A api = provider.find(entity, context);
				if (api != null) return api;
			}

			return null;
		});
	}

	private final Identifier identifier;
	private final Class<A> apiClass;
	private final Class<C> contextClass;
	private final Event<EntityApiProvider<A, C>> preliminary = newEvent();
	private final ApiProviderMap<EntityType<?>, Event<EntityApiProvider<A, C>>> typeSpecific = ApiProviderMap.create();
	/**
	 * It can't reflect phase order.<br/>
	 * It's just for {@link #getProvider}. It should be removed in the future.
	 */
	@ApiStatus.Experimental
	private final Multimap<EntityType<?>, EntityApiProvider<A, C>> typeSpecificProviders = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
	private final Event<EntityApiProvider<A, C>> fallback = newEvent();

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
				for (Map.Entry<Class<?>, Collection<EntityType<?>>> entry : REGISTERED_SELVES.asMap().entrySet()) {
					Class<?> apiClass = entry.getKey();

					for (EntityType<?> entityType : entry.getValue()) {
						Entity entity = entityType.create(server.getOverworld());

						if (entity == null) {
							String errorMessage = String.format(
									"Failed to register self-implementing entities for API class %s. Can not create entity of type %s.",
									apiClass.getCanonicalName(),
									Registries.ENTITY_TYPE.getId(entityType)
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
				}
			}
		}
	}

	@Override
	public void registerSelf(EntityType<?>... entityTypes) {
		synchronized (REGISTERED_SELVES) {
			REGISTERED_SELVES.putAll(apiClass(), Arrays.asList(entityTypes));
		}

		EntityApiLookup.super.registerSelf(entityTypes);
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

	@SuppressWarnings("removal")
	@Override
	@Deprecated(forRemoval = true)
	public @Nullable EntityApiProvider<A, C> getProvider(EntityType<?> entityType) {
		for (EntityApiProvider<A, C> provider : typeSpecificProviders.get(entityType)) {
			return provider;
		}

		return null;
	}

	@Override
	public Event<EntityApiProvider<A, C>> preliminary() {
		return preliminary;
	}

	@Override
	public @UnmodifiableView Map<EntityType<?>, Event<EntityApiProvider<A, C>>> typeSpecific() {
		return typeSpecific.asMap();
	}

	@Override
	public @NotNull Event<EntityApiProvider<A, C>> getSpecificFor(EntityType<?> type) {
		Event<EntityApiProvider<A, C>> event = typeSpecific.get(type);

		if (event == null) {
			typeSpecific.putIfAbsent(type, newEvent());
			event = Objects.requireNonNull(typeSpecific.get(type));
		}

		return event;
	}

	@Override
	public Event<EntityApiProvider<A, C>> fallback() {
		return fallback;
	}
}
