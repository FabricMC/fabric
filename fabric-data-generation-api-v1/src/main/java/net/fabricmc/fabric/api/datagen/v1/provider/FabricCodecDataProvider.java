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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

/**
 * Extend this class and implement {@link FabricCodecDataProvider#configure(BiConsumer, RegistryWrapper.WrapperLookup)}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class FabricCodecDataProvider<T> implements DataProvider {
	private final DataOutput.PathResolver pathResolver;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
	private final Codec<T> codec;

	/**
	 * @deprecated Please use {@link FabricCodecDataProvider#FabricCodecDataProvider(FabricDataOutput, CompletableFuture, DataOutput.OutputType, String, Codec)}.
	 */
	@Deprecated(forRemoval = true)
	protected FabricCodecDataProvider(FabricDataOutput dataOutput, DataOutput.OutputType outputType, String directoryName, Codec<T> codec) {
		this.pathResolver = dataOutput.getResolver(outputType, directoryName);
		this.registriesFuture = null;
		this.codec = codec;
	}

	protected FabricCodecDataProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, DataOutput.OutputType outputType, String directoryName, Codec<T> codec) {
		this.pathResolver = dataOutput.getResolver(outputType, directoryName);
		this.registriesFuture = Objects.requireNonNull(registriesFuture);
		this.codec = codec;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		// TODO: Remove the null check once the deprecated method and constructor are removed.
		if (this.registriesFuture != null) {
			return this.registriesFuture.thenCompose(lookup -> {
				Map<Identifier, JsonElement> entries = new HashMap<>();
				RegistryOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, lookup);

				BiConsumer<Identifier, T> provider = (id, value) -> {
					JsonElement json = this.convert(id, value, ops);
					JsonElement existingJson = entries.put(id, json);

					if (existingJson != null) {
						throw new IllegalArgumentException("Duplicate entry " + id);
					}
				};

				this.configure(provider, lookup);
				return this.write(writer, entries);
			});
		}

		Map<Identifier, JsonElement> entries = new HashMap<>();
		BiConsumer<Identifier, T> provider = (id, value) -> {
			JsonElement json = this.convert(id, value);
			JsonElement existingJson = entries.put(id, json);

			if (existingJson != null) {
				throw new IllegalArgumentException("Duplicate entry " + id);
			}
		};

		this.configure(provider);
		return this.write(writer, entries);
	}

	/**
	 * Implement this method to register entries to generate.
	 *
	 * @param provider A consumer that accepts an {@link Identifier} and a value to register.
	 * @deprecated Please use {@link FabricCodecDataProvider#configure(BiConsumer, RegistryWrapper.WrapperLookup)}.
	 */
	@Deprecated(forRemoval = true)
	protected void configure(BiConsumer<Identifier, T> provider) {
	}

	/**
	 * Implement this method to register entries to generate using a {@link RegistryWrapper.WrapperLookup}.
	 * @param provider A consumer that accepts an {@link Identifier} and a value to register.
	 * @param lookup A lookup for registries.
	 */
	protected void configure(BiConsumer<Identifier, T> provider, RegistryWrapper.WrapperLookup lookup) {
		// TODO: Make abstract once the deprecated method is removed.
	}

	private JsonElement convert(Identifier id, T value) {
		return this.convert(id, value, JsonOps.INSTANCE);
	}

	private JsonElement convert(Identifier id, T value, DynamicOps<JsonElement> ops) {
		DataResult<JsonElement> dataResult = this.codec.encodeStart(ops, value);
		return dataResult.get()
				.mapRight(partial -> "Invalid entry %s: %s".formatted(id, partial.message()))
				.orThrow();
	}

	private CompletableFuture<?> write(DataWriter writer, Map<Identifier, JsonElement> entries) {
		return CompletableFuture.allOf(entries.entrySet().stream().map(entry -> {
			Path path = this.pathResolver.resolveJson(entry.getKey());
			return DataProvider.writeToPath(writer, entry.getValue(), path);
		}).toArray(CompletableFuture[]::new));
	}
}
