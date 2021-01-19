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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.mixin.object.builder.ActivityAccessor;
import net.fabricmc.fabric.mixin.object.builder.MemoryModuleTypeAccessor;
import net.fabricmc.fabric.mixin.object.builder.SensorTypeAccessor;

/**
 * This class provides utilities to create {@link Activity} and {@link SensorType} and {@link MemoryModuleType}.
 *
 * <p>An activity is a logical group of tasks.
 * Tasks are grouped by activity and only tasks associated with current or core activities are executed.
 *
 * <p>A sensor is used to trigger brain tasks.
 * Each sensor can read and/or write to any requested and presented memory module in a given entity.
 *
 * <p>A memory module is used to store sensor outputs and tasks intermediary data.
 * Each task can read and/or write to any requested and presented memory module in a given entity.
 */
public final class EntityBrainHelper {
	private EntityBrainHelper() {
	}

	/**
	 * Creates and registers an {@link Activity}.
	 *
	 * @param id the id of this {@link Activity}.
	 * @return a new {@link Activity}.
	 */
	public static Activity registerActivity(Identifier id) {
		Objects.requireNonNull(id, "Activity ID cannot be null");

		return Registry.register(Registry.ACTIVITY, id, ActivityAccessor.init(id.toString()));
	}

	/**
	 * Creates and registers a {@link SensorType}.
	 *
	 * @param id the id of this {@link SensorType}.
	 * @param sensorFactory a supplier to provide a {@link Sensor}.
	 * @return a new {@link SensorType}.
	 */
	public static <U extends Sensor<?>> SensorType<U> registerSensorType(Identifier id, Supplier<U> sensorFactory) {
		Objects.requireNonNull(id, "Sensor ID cannot be null");
		Objects.requireNonNull(sensorFactory, "Sensor factory cannot be null");
		Objects.requireNonNull(sensorFactory.get(), "Sensor factory cannot return null");

		return Registry.register(Registry.SENSOR_TYPE, id, SensorTypeAccessor.init(sensorFactory));
	}

	/**
	 * Creates and registers a {@link MemoryModuleType}.
	 *
	 * @param id the id of this {@link MemoryModuleType}.
	 * @return a new {@link MemoryModuleType}.
	 */
	public static <U> MemoryModuleType<U> registerMemoryModuleType(Identifier id) {
		return registerMemoryModuleType(id, null);
	}

	/**
	 * Creates and registers a {@link MemoryModuleType}.
	 *
	 * @param id the id of this {@link MemoryModuleType}.
	 * @param codec {@link Codec} used to serialize and deserialize actual memory modules.
	 * @return a new {@link MemoryModuleType}.
	 * @implNote codec can be null, though then the shortcut method should be used instead.
	 */
	public static <U> MemoryModuleType<U> registerMemoryModuleType(Identifier id, @Nullable Codec<U> codec) {
		Objects.requireNonNull(id, "Memory module ID cannot be null");

		return Registry.register(Registry.MEMORY_MODULE_TYPE, id, MemoryModuleTypeAccessor.init(Optional.ofNullable(codec)));
	}
}
