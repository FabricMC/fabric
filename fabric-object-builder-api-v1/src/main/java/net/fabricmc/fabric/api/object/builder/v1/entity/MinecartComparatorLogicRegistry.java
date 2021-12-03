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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.registry.Registry;

/**
 * A registry for {@linkplain MinecartComparatorLogic custom minecart compator logic}.
 */
public final class MinecartComparatorLogicRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<EntityType<?>, MinecartComparatorLogic<?>> logics = new HashMap<>();

	/**
	 * Gets the registered custom comparator logic for the specified minecart entity type.
	 *
	 * @param type the entity type
	 * @return the comparator logic, or {@code null} if not registered
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static MinecartComparatorLogic<AbstractMinecartEntity> getCustomComparatorLogic(EntityType<?> type) {
		return (MinecartComparatorLogic<AbstractMinecartEntity>) logics.get(type);
	}

	/**
	 * Registers a comparator logic for a minecart entity type.
	 *
	 * <p>Registering a second value for an entity type will replace the old logic.
	 *
	 * @param <T>   the handled minecart type
	 * @param type  the minecart entity type
	 * @param logic the logic to register
	 */
	public static <T extends AbstractMinecartEntity> void register(EntityType<T> type, MinecartComparatorLogic<? super T> logic) {
		if (logics.put(type, logic) != null) {
			LOGGER.warn("Overriding existing minecart comparator logic for entity type {}", Registry.ENTITY_TYPE.getId(type));
		}
	}
}
