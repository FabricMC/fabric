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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.class_7871;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.report.WorldgenProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLoader;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

@ApiStatus.Experimental
public abstract class FabricWorldgenProvider implements DataProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(WorldgenProvider.class);

	private final FabricDataOutput output;
	private final CompletableFuture<CommandRegistryWrapper.class_7874> registriesFuture;

	public FabricWorldgenProvider(FabricDataOutput output,
									CompletableFuture<CommandRegistryWrapper.class_7874> registriesFuture) {
		this.output = output;
		this.registriesFuture = registriesFuture;
	}

	protected abstract void configure(CommandRegistryWrapper.class_7874 registries, Entries entries);

	public static final class Entries {
		private final CommandRegistryWrapper.class_7874 registries;
		private final Map<RegistryKey<? extends Registry<?>>, RegistryEntries<?>> queuedEntries;

		@ApiStatus.Internal
		Entries(CommandRegistryWrapper.class_7874 registries) {
			this.registries = registries;
			this.queuedEntries = RegistryLoader.DYNAMIC_REGISTRIES.stream()
					.collect(Collectors.toMap(
							RegistryLoader.Entry::key,
							RegistryEntries::create
					));
		}

		public class_7871<PlacedFeature> placedFeatures() {
			return registries.method_46762(Registry.PLACED_FEATURE_KEY);
		}

		public class_7871<ConfiguredCarver<?>> configuredCarvers() {
			return registries.method_46762(Registry.CONFIGURED_CARVER_KEY);
		}

		public <T> RegistryEntries<T> of(RegistryKey<? extends Registry<T>> registry) {
			return getQueuedEnries(registry);
		}

		@SuppressWarnings("unchecked")
		@ApiStatus.Internal
		<T> RegistryEntries<T> getQueuedEnries(RegistryKey<? extends Registry<T>> registryKey) {
			RegistryEntries<?> regEntries = queuedEntries.get(registryKey);

			if (regEntries == null) {
				throw new IllegalArgumentException("Registry " + registryKey + " is not loaded from datapacks");
			}

			return (RegistryEntries<T>) regEntries;
		}
	}

	public static class RegistryEntries<T> {
		final RegistryKey<? extends Registry<T>> registry;
		final Codec<T> elementCodec;
		List<Entry<T>> entries = new ArrayList<>();

		public RegistryEntries(RegistryKey<? extends Registry<T>> registry, Codec<T> elementCodec) {
			this.registry = registry;
			this.elementCodec = elementCodec;
		}

		public static <T> RegistryEntries<T> create(RegistryLoader.Entry<T> loaderEntry) {
			return new RegistryEntries<>(loaderEntry.key(), loaderEntry.elementCodec());
		}

		public RegistryEntries<T> add(RegistryKey<T> key, T value) {
			entries.add(new Entry<>(key, value));
			return this;
		}

		public RegistryEntries<T> add(Identifier id, T value) {
			entries.add(new Entry<>(RegistryKey.of(registry, id), value));
			return this;
		}

		record Entry<T>(RegistryKey<T> key, T value) {
		}
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return registriesFuture.thenCompose(registries -> {
			return CompletableFuture
					.supplyAsync(() -> {
						var entries = new Entries(registries);
						configure(registries, entries);
						return entries;
					})
					.thenCompose(entries -> {
						final RegistryOps<JsonElement> dynamicOps = RegistryOps.method_46632(JsonOps.INSTANCE, registries);
						ArrayList<CompletableFuture<?>> futures = new ArrayList<>();

						for (RegistryEntries<?> registryEntries : entries.queuedEntries.values()) {
							futures.add(writeRegistryEntries(writer, dynamicOps, registryEntries));
						}

						return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
					});
		});
	}

	private <T> CompletableFuture<?> writeRegistryEntries(DataWriter writer, RegistryOps<JsonElement> ops, RegistryEntries<T> entries) {
		final RegistryKey<? extends Registry<T>> registry = entries.registry;
		final DataOutput.PathResolver pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, registry.getValue().getPath());
		final List<CompletableFuture<?>> futures = new ArrayList<>();

		for (RegistryEntries.Entry<T> entry : entries.entries) {
			RegistryKey<T> key = entry.key;

			Path path = pathResolver.resolveJson(key.getValue());
			futures.add(writeToPath(path, writer, ops, entries.elementCodec, entry.value));
		}

		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	private static <E> CompletableFuture<?> writeToPath(Path path, DataWriter cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
		Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial((error) -> {
			LOGGER.error("Couldn't serialize element {}: {}", path, error);
		});

		if (optional.isPresent()) {
			return DataProvider.writeToPath(cache, optional.get(), path);
		}

		return CompletableFuture.completedFuture(null);
	}
}
