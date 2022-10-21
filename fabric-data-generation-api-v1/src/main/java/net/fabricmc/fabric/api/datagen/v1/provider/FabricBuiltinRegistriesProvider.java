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

package net.fabricmc.fabric.api.datagen.v1.provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.class_7655;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.report.WorldgenProvider;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Data-generates all entries from {@link BuiltinRegistries} that matches a given filter (i.e. mod id).
 *
 * @see WorldgenProvider For Vanilla's data provider that does the same for the entire registry.
 */
public class FabricBuiltinRegistriesProvider implements DataProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(FabricBuiltinRegistriesProvider.class);

	private final Predicate<RegistryKey<?>> entryFilter;
	private final DataOutput output;

	private FabricBuiltinRegistriesProvider(DataOutput output, Predicate<RegistryKey<?>> entryFilter) {
		this.output = output;
		this.entryFilter = entryFilter;
	}

	/**
	 * @return A provider that will export all entries from {@link BuiltinRegistries} for the mod running the
	 * data generation.
	 */
	public static Function<FabricDataGenerator, DataProvider> forCurrentMod() {
		return fabricDataGenerator -> new FabricBuiltinRegistriesProvider(
				fabricDataGenerator.getOutput(),
				e -> e.getValue().getNamespace().equals(fabricDataGenerator.getModId())
		);
	}

	public void run(DataWriter writer) {
		DynamicRegistryManager dynamicRegistryManager = BuiltinRegistries.method_45968();
		DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
		class_7655.field_39968.forEach((info) -> this.writeRegistryEntries(writer, dynamicRegistryManager, dynamicOps, info));
	}

	private <T> void writeRegistryEntries(DataWriter writer, DynamicRegistryManager registryManager, DynamicOps<JsonElement> ops, class_7655.class_7657<T> registry) {
		RegistryKey<? extends Registry<T>> registryKey = registry.key();
		Registry<T> registry2 = registryManager.get(registryKey);
		DataOutput.PathResolver pathResolver = this.output.getResolver(DataOutput.OutputType.DATA_PACK, registryKey.getValue().getPath());

		for (Map.Entry<RegistryKey<T>, T> regEntry : registry2.getEntrySet()) {
			RegistryKey<T> key = regEntry.getKey();

			if (!entryFilter.test(key)) {
				continue;
			}

			Path path = pathResolver.resolveJson(key.getValue());
			writeToPath(path, writer, ops, registry.elementCodec(), regEntry.getValue());
		}
	}

	private static <E> void writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		try {
			Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial((error) -> {
				LOGGER.error("Couldn't serialize element {}: {}", path, error);
			});

			if (optional.isPresent()) {
				DataProvider.writeToPath(cache, optional.get(), path);
			}
		} catch (IOException var6) {
			LOGGER.error("Couldn't save element {}", path, var6);
		}
	}

	public String getName() {
		return "Built-In Registry Content";
	}
}
