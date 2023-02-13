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

package net.fabricmc.fabric.impl.datagen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public final class FabricDataGenHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(FabricDataGenHelper.class);

	/**
	 * When enabled the dedicated server startup will be hijacked to run the data generators and then quit.
	 */
	public static final boolean ENABLED = System.getProperty("fabric-api.datagen") != null;

	/**
	 * Sets the output directory for the generated data.
	 */
	private static final String OUTPUT_DIR = System.getProperty("fabric-api.datagen.output-dir");

	/**
	 * When enabled providers can enable extra validation, such as ensuring all registry entries have data generated for them.
	 */
	private static final boolean STRICT_VALIDATION = System.getProperty("fabric-api.datagen.strict-validation") != null;

	/**
	 * Filter to a specific mod ID with this property, useful if dependencies also have data generators.
	 */
	@Nullable
	private static final String MOD_ID_FILTER = System.getProperty("fabric-api.datagen.modid");

	/**
	 * Entrypoint key to register classes implementing {@link DataGeneratorEntrypoint}.
	 */
	private static final String ENTRYPOINT_KEY = "fabric-datagen";

	private FabricDataGenHelper() {
	}

	public static void run() {
		try {
			runInternal();
		} catch (Throwable t) {
			LOGGER.error(LogUtils.FATAL_MARKER, "Failed to run data generation", t);

			// Ensure we exit with a none zero exit code.
			System.exit(-1);
		}
	}

	private static void runInternal() {
		Path outputDir = Paths.get(Objects.requireNonNull(OUTPUT_DIR, "No output dir provided with the 'fabric-api.datagen.output-dir' property"));

		List<EntrypointContainer<DataGeneratorEntrypoint>> dataGeneratorInitializers = FabricLoader.getInstance()
				.getEntrypointContainers(ENTRYPOINT_KEY, DataGeneratorEntrypoint.class);

		if (dataGeneratorInitializers.isEmpty()) {
			LOGGER.warn("No data generator entrypoints are defined. Implement {} and add your class to the '{}' entrypoint key in your fabric.mod.json.",
					DataGeneratorEntrypoint.class.getName(), ENTRYPOINT_KEY);
		}

		// Ensure that the DataGeneratorEntrypoint is constructed on the main thread.
		final List<DataGeneratorEntrypoint> entrypoints = dataGeneratorInitializers.stream().map(EntrypointContainer::getEntrypoint).toList();
		CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture = CompletableFuture.supplyAsync(() -> createRegistryWrapper(entrypoints), Util.getMainWorkerExecutor());

		for (EntrypointContainer<DataGeneratorEntrypoint> entrypointContainer : dataGeneratorInitializers) {
			final String id = entrypointContainer.getProvider().getMetadata().getId();

			if (MOD_ID_FILTER != null) {
				if (!id.equals(MOD_ID_FILTER)) {
					continue;
				}
			}

			LOGGER.info("Running data generator for {}", id);

			try {
				final DataGeneratorEntrypoint entrypoint = entrypointContainer.getEntrypoint();
				final String effectiveModId = entrypoint.getEffectiveModId();
				ModContainer modContainer = entrypointContainer.getProvider();

				if (effectiveModId != null) {
					modContainer = FabricLoader.getInstance().getModContainer(effectiveModId).orElseThrow(() -> new RuntimeException("Failed to find effective mod container for mod id (%s)".formatted(effectiveModId)));
				}

				FabricDataGenerator dataGenerator = new FabricDataGenerator(outputDir, modContainer, STRICT_VALIDATION, registriesFuture);
				entrypoint.onInitializeDataGenerator(dataGenerator);
				dataGenerator.run();
			} catch (Throwable t) {
				throw new RuntimeException("Failed to run data generator from mod (%s)".formatted(id), t);
			}
		}
	}

	private static RegistryWrapper.WrapperLookup createRegistryWrapper(List<DataGeneratorEntrypoint> dataGeneratorInitializers) {
		// Build a list of all the RegistryBuilder's including vanilla's
		List<RegistryBuilder> builders = new ArrayList<>();
		builders.add(BuiltinRegistries.REGISTRY_BUILDER);

		for (DataGeneratorEntrypoint entrypoint : dataGeneratorInitializers) {
			final var registryBuilder = new RegistryBuilder();
			entrypoint.buildRegistry(registryBuilder);
			builders.add(registryBuilder);
		}

		// Collect all the bootstrap functions, and merge the lifecycles.
		class BuilderData {
			final RegistryKey key;
			List<RegistryBuilder.BootstrapFunction<?>> bootstrapFunctions;
			Lifecycle lifecycle;

			BuilderData(RegistryKey key) {
				this.key = key;
				this.bootstrapFunctions = new ArrayList<>();
				this.lifecycle = null;
			}

			void with(RegistryBuilder.RegistryInfo<?> registryInfo) {
				bootstrapFunctions.add(registryInfo.bootstrap());
				lifecycle = registryInfo.lifecycle().add(lifecycle);
			}

			void apply(RegistryBuilder builder) {
				builder.addRegistry(key, lifecycle, this::bootstrap);
			}

			void bootstrap(Registerable registerable) {
				for (RegistryBuilder.BootstrapFunction<?> function : bootstrapFunctions) {
					function.run(registerable);
				}
			}
		}

		Map<RegistryKey<?>, BuilderData> builderDataMap = new HashMap<>();

		for (RegistryBuilder builder : builders) {
			for (RegistryBuilder.RegistryInfo<?> info : builder.registries) {
				builderDataMap.computeIfAbsent(info.key(), BuilderData::new)
						.with(info);
			}
		}

		// Apply all the builders into one.
		RegistryBuilder merged = new RegistryBuilder();

		for (BuilderData value : builderDataMap.values()) {
			value.apply(merged);
		}

		RegistryWrapper.WrapperLookup wrapperLookup = merged.createWrapperLookup(DynamicRegistryManager.of(Registries.REGISTRIES));
		BuiltinRegistries.validate(wrapperLookup);
		return wrapperLookup;
	}

	/**
	 * Used to keep track of conditions associated to generated objects.
	 */
	private static final Map<Object, ConditionJsonProvider[]> CONDITIONS_MAP = new IdentityHashMap<>();

	public static void addConditions(Object object, ConditionJsonProvider[] conditions) {
		CONDITIONS_MAP.merge(object, conditions, ArrayUtils::addAll);
	}

	@Nullable
	public static ConditionJsonProvider[] consumeConditions(Object object) {
		return CONDITIONS_MAP.remove(object);
	}
}
