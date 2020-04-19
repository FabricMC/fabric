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

package net.fabricmc.fabric.impl.entity.attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.entity.attribute.v1.EntityAttributeRegistry;

public final class FabricEntityAttributeRegistry implements EntityAttributeRegistry {
	public static final FabricEntityAttributeRegistry INSTANCE = new FabricEntityAttributeRegistry();
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<EntityType<? extends LivingEntity>, Supplier<DefaultAttributeContainer.Builder>> pendingRegistrations = new HashMap<>();
	private Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> registrations;

	@Override
	public void register(EntityType<? extends LivingEntity> type, Supplier<DefaultAttributeContainer.Builder> builderSupplier) {
		if (this.registrations == null) {
			if (this.pendingRegistrations.put(type, builderSupplier) != null) {
				LOGGER.info("Overriding existing registration for entity type {}", Registry.ENTITY_TYPE.getId(type));
			}

			return;
		}

		if (this.registrations.put(type, builderSupplier.get().build()) != null) {
			LOGGER.info("Overriding existing registration for entity type {}", Registry.ENTITY_TYPE.getId(type));
		}
	}

	public void initMap(Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> map) {
		for (Map.Entry<EntityType<? extends LivingEntity>, Supplier<DefaultAttributeContainer.Builder>> entry : this.pendingRegistrations.entrySet()) {
			EntityType<? extends LivingEntity> type = entry.getKey();

			if (map.put(type, entry.getValue().get().build()) != null) {
				LOGGER.info("Overriding existing registration for entity type {}", Registry.ENTITY_TYPE.getId(type));
			}
		}

		this.registrations = map;
	}
}
