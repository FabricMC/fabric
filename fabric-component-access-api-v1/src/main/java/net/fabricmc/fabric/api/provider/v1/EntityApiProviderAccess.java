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

import net.fabricmc.fabric.impl.provider.ApiProviderAccessRegistry;
import net.fabricmc.fabric.impl.provider.EntityApiProviderAccessImpl;

/**
 * Describes and provides access to component instances that may be retrieved
 * for blocks, items or entities.
 *
 * <p>This interface should never be implemented by mod authors. Create new instances
 * using {@link ApiProviderAccessRegistry#createAccess(net.minecraft.util.Identifier, Class, ApiProvider)}.
 *
 * @param <P> Identifies the API provider type
 * @param <A> Identifies the API type
 */
public interface EntityApiProviderAccess<P extends ApiProvider<P, A>, A> extends ApiProviderAccess<P, A> {
	/**
	 * Causes the given entity types to provide component instances of this type
	 * by application of the given mapping function.
	 *
	 * <p>This will override any previous mapping of the same component type and only one
	 * result per entity is possible.  For the reason, mod authors are advised to create
	 * distinct component types for their use cases as needed to prevent conflicts.
	 *
	 * @param mapping mapping function that derives a component instance from an access context
	 * @param entities one or more entities for which the function will apply
	 */
	void registerProviderForEntity(Function<Entity, P> mapping, EntityType<?> entityType);

	/**
	 * Retrieves a {@code ComponentAccess} to access components of this type
	 * that may be present in the given entity.
	 *
	 * <p>If the API consumer somehow knows the entity implements the
	 * provider interface directly, casting the entity instance will always be
	 * faster. This is useful when that is unknown to the consumer, or when
	 * the Entity exposes the target API as a member.
	 *
	 * @param entity entity to provide component access if available
	 * @return a {@code ComponentAccess} to access components of this type
	 * that may be present in the given entity
	 */
	P getProviderFromEntity(Entity entity);

	static <P extends ApiProvider<P, A>, A> EntityApiProviderAccess<P, A> registerAcess(Identifier id, Class<A> apiType, P absentProvider) {
		return EntityApiProviderAccessImpl.registerAcess(id, apiType, absentProvider);
	}

	static EntityApiProviderAccess<?, ?> getAccess(Identifier id) {
		return EntityApiProviderAccessImpl.getAccess(id);
	}
}
