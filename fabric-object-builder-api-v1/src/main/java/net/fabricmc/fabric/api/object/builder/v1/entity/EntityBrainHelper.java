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

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.mixin.object.builder.MemoryModuleTypeAccessor;
import net.fabricmc.fabric.mixin.object.builder.SensorTypeAccessor;

/**
 * This class provides utilities to create {@link SensorType} and {@link MemoryModuleType}.
 *
 * <p>A sensor is used to trigger brain tasks.
 * Each task can check the output value of each presented sensor in a given entity.
 *
 * <p>A memory module is used to store brain task results and intermediary data.
 * Each task can check the stored value of each presented memory module in a given entity.
 */
public final class EntityBrainHelper {
	private EntityBrainHelper() {
	}

	/**
	 * Creates and registers a {@link SensorType}.
	 *
	 * @param id the id of this {@link SensorType}.
	 * @param sensorFactory a supplier to provide a {@link Sensor}.
	 * @return a new {@link SensorType}.
	 */
	public static <U extends Sensor<?>> SensorType<U> registerSensorType(Identifier id, Supplier<U> sensorFactory) {
		return Registry.register(Registry.SENSOR_TYPE, id, SensorTypeAccessor.init(sensorFactory));
	}

	/**
	 * Creates and registers a {@link MemoryModuleType}.
	 *
	 * @param id the id of this {@link MemoryModuleType}.
	 * @return a new {@link MemoryModuleType}.
	 */
	public static <U> MemoryModuleType<U> registerMemoryModuleType(Identifier id) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, id, MemoryModuleTypeAccessor.init(Optional.empty()));
	}

	/**
	 * Creates and registers a {@link MemoryModuleType}.
	 *
	 * @param id the id of this {@link MemoryModuleType}.
	 * @param codec {@link Codec} used to serialize and deserialize actual memory modules.
	 * @return a new {@link MemoryModuleType}.
	 */
	public static <U> MemoryModuleType<U> registerMemoryModuleType(Identifier id, Codec<U> codec) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, id, MemoryModuleTypeAccessor.init(Optional.of(codec)));
	}
}
