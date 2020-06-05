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

package net.fabricmc.fabric.api.object.builder.v1.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;

/**
 * Allows registering custom default attributes for living entities.
 *
 * <p>All living entity types must have default attributes registered. See {@link
 * FabricEntityTypeBuilder} for utility on entity type registration in general.</p>
 *
 * <p>A registered default attribute for an entity type can be retrieved through
 * {@link net.minecraft.entity.attribute.DefaultAttributeRegistry#get(EntityType)}.</p>
 *
 * @see net.minecraft.entity.attribute.DefaultAttributeRegistry
 */
public final class FabricDefaultAttributeRegistry {
	/**
	 * Private logger, not exposed.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Registers a default attribute for a type of living entity.
	 *
	 * <p>It can be used in a fashion similar to this:
	 * <blockquote><pre>
	 * EntityAttributeRegistry.INSTANCE.register(type, LivingEntity.createLivingAttributes());
	 * </pre></blockquote>
	 * </p>
	 *
	 * <p>If a registration overrides another, a debug log message will be emitted. Existing registrations
	 * can be checked at {@link net.minecraft.entity.attribute.DefaultAttributeRegistry#hasDefinitionFor(EntityType)}.</p>
	 *
	 * @param type    the entity type
	 * @param builder the builder that creates the default attribute
	 */
	public static void register(EntityType<? extends LivingEntity> type, DefaultAttributeContainer.Builder builder) {
		if (DefaultAttributeRegistryAccessor.getRegistry().put(type, builder.build()) != null) {
			LOGGER.debug("Overriding existing registration for entity type {}", Registry.ENTITY_TYPE.getId(type));
		}
	}
}
