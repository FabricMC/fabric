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

package net.fabricmc.fabric.api.entity.attribute.v1;

import java.util.function.Supplier;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import net.fabricmc.fabric.impl.entity.attribute.FabricEntityAttributeRegistry;

/**
 * Allows registering custom default attributes for living entities.
 *
 * <p>All living entity types must have default attributes registered.</p>
 *
 * <p>A registered default attribute for an entity type can be retrieved through
 * {@link net.minecraft.entity.attribute.DefaultAttributeRegistry#get(EntityType)}.</p>
 *
 * @see net.minecraft.entity.attribute.DefaultAttributeRegistry
 * @deprecated Vanilla snapshot feature, subject to vanilla change
 */
@Deprecated
public interface EntityAttributeRegistry {
	/**
	 * The entity attribute registry provided by the Fabric API.
	 */
	EntityAttributeRegistry INSTANCE = FabricEntityAttributeRegistry.INSTANCE;

	/**
	 * Registers a default attribute for a type of living entity.
	 *
	 * <p>It can be used in a fashion similar to this:
	 * <blockquote><pre>
	 * EntityAttributeRegistry.INSTANCE.register(type, LivingEntity::createLivingAttributes);
	 * </pre></blockquote>
	 * </p>
	 *
	 * <p>If a registration is too late or duplicates a custom registration, the registry
	 * will throw an exception. If a registration duplicates a vanilla registration, the
	 * registry will fail when vanilla finishes the registration.</p>
	 *
	 * @param type            the entity type
	 * @param builderSupplier the supplier giving a builder that creates the default attribute
	 * @throws IllegalStateException    if the registration is too late
	 * @throws IllegalArgumentException if a registration duplicates a previous registration
	 */
	void register(EntityType<? extends LivingEntity> type, Supplier<DefaultAttributeContainer.Builder> builderSupplier);
}
