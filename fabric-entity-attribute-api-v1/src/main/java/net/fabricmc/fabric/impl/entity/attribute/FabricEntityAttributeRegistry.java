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

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.entity.attribute.v1.EntityAttributeRegistry;

public final class FabricEntityAttributeRegistry implements EntityAttributeRegistry {
	public static final FabricEntityAttributeRegistry INSTANCE = new FabricEntityAttributeRegistry();
	private final Map<EntityType<? extends LivingEntity>, Supplier<DefaultAttributeContainer.Builder>> registrations = new HashMap<>();
	private boolean invalid = false;

	@Override
	public void register(EntityType<? extends LivingEntity> type, Supplier<DefaultAttributeContainer.Builder> builderSupplier) {
		if (this.invalid) {
			throw new IllegalStateException("Registering default living attribute too late!");
		}

		if (this.registrations.put(type, builderSupplier) != null) {
			throw new IllegalArgumentException(String.format("Duplicate registration for entity type %s (id \"%s\")", type, Registry.ENTITY_TYPE.getId(type)));
		}
	}

	public void registerTo(ImmutableMap.Builder<EntityType<? extends LivingEntity>, DefaultAttributeContainer> builder) {
		this.invalid = true;

		for (Map.Entry<EntityType<? extends LivingEntity>, Supplier<DefaultAttributeContainer.Builder>> entry : this.registrations.entrySet()) {
			builder.put(entry.getKey(), entry.getValue().get().build());
		}
	}
}
