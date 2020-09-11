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

package net.fabricmc.fabric.api.provider.v1;

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.provider.EntityApiProviderAccessImpl;

/**
 * See {link ApiProviderAccess}. This subclass is for {@code Entity} game objects.
 */
public interface EntityApiProviderAccess<P extends ApiProvider<P, A>, A> extends ApiProviderAccess<P, A> {
	/**
	 * Causes the given entities to to supply API provider instances by application of
	 * the given mapping function.
	 *
	 * <p>The mapping function should return {@link #absentApi()} if no component is available.
	 *
	 * @param mapping function that derives a provider instance from an entity
	 * @param entityType type for which the mapping will apply
	 */
	void registerProviderForEntity(Function<Entity, P> mapping, EntityType<?> entityType);

	/**
	 * Retrieves an {@code ApiProvider} used to obtain an API instance if present.
	 *
	 * <p>If the API consumer somehow knows the entity consistently implements the
	 * API or provider interface directly, casting the entity instance will always be faster.
	 *
	 * @param entity the entity where the component may be located
	 * @return a {@code ApiProvider} used to obtain an API instance if present.
	 * Will be {@link #absentProvider()} if no API is present.
	 */
	P getProviderFromEntity(Entity entity);

	static <P extends ApiProvider<P, A>, A> EntityApiProviderAccess<P, A> registerAcess(Identifier id, Class<A> apiType, P absentProvider) {
		return EntityApiProviderAccessImpl.registerAcess(id, apiType, absentProvider);
	}

	static EntityApiProviderAccess<?, ?> getAccess(Identifier id) {
		return EntityApiProviderAccessImpl.getAccess(id);
	}
}
