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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.mojang.serialization.Lifecycle;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.DynamicRegistryTagProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

@ApiStatus.Internal
public final class FabricDataGenHelper {
	private static final Logger LOGGER = LogManager.getLogger();

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

	/**
	 * A fake registry instance to be used for {@link DynamicRegistryTagProvider}.
	 *
	 * <p>In {@link AbstractTagProvider#run}, it checks for whether the registry has all the elements added to the builder.
	 * This would be fine for static registry, but there won't be any instance dynamic registry available.
	 * Therefore, this simply return true for all {@link Registry#containsId} call.
	 */
	@SuppressWarnings("rawtypes")
	private static final Registry FAKE_DYNAMIC_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier("fabric:fake_dynamic_registry")), Lifecycle.experimental()) {
		@Override
		public boolean containsId(Identifier id) {
			return true;
		}
	};

	private FabricDataGenHelper() {
	}

	public static void run() throws IOException {
		Path outputDir = Paths.get(Objects.requireNonNull(OUTPUT_DIR, "No output dir provided with the 'fabric-api.datagen.output-dir' property"));

		List<EntrypointContainer<DataGeneratorEntrypoint>> dataGeneratorInitializers = FabricLoader.getInstance()
				.getEntrypointContainers(ENTRYPOINT_KEY, DataGeneratorEntrypoint.class);

		if (dataGeneratorInitializers.isEmpty()) {
			LOGGER.warn("No data generator entrypoints are defined. Implement {} and add your class to the '{}' entrypoint key in your fabric.mod.json.",
					DataGeneratorEntrypoint.class.getName(), ENTRYPOINT_KEY);
		}

		for (EntrypointContainer<DataGeneratorEntrypoint> entrypointContainer : dataGeneratorInitializers) {
			if (MOD_ID_FILTER != null) {
				if (!entrypointContainer.getProvider().getMetadata().getId().equals(MOD_ID_FILTER)) {
					continue;
				}
			}

			LOGGER.info("Running data generator for {}", entrypointContainer.getProvider().getMetadata().getName());
			FabricDataGenerator dataGenerator = new FabricDataGenerator(outputDir, entrypointContainer.getProvider(), STRICT_VALIDATION);
			entrypointContainer.getEntrypoint().onInitializeDataGenerator(dataGenerator);
			dataGenerator.run();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Registry<T> getFakeDynamicRegistry() {
		return FAKE_DYNAMIC_REGISTRY;
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
