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
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
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

	private FabricCodecDataProvider(DataOutput.PathResolver pathResolver, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, Codec<T> codec) {
		this.pathResolver = pathResolver;
		this.registriesFuture = Objects.requireNonNull(registriesFuture);
		this.codec = codec;
	}

	protected FabricCodecDataProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, DataOutput.OutputType outputType, String directoryName, Codec<T> codec) {
		this(dataOutput.getResolver(outputType, directoryName), registriesFuture, codec);
	}

	protected FabricCodecDataProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, RegistryKey<? extends Registry<?>> key, Codec<T> codec) {
		this(dataOutput.getResolver(key), registriesFuture, codec);
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registriesFuture.thenCompose(lookup -> {
			Map<Identifier, JsonElement> entries = new HashMap<>();
			RegistryOps<JsonElement> ops = lookup.getOps(JsonOps.INSTANCE);

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

	/**
	 * Implement this method to register entries to generate using a {@link RegistryWrapper.WrapperLookup}.
	 * @param provider A consumer that accepts an {@link Identifier} and a value to register.
	 * @param lookup A lookup for registries.
	 */
	protected abstract void configure(BiConsumer<Identifier, T> provider, RegistryWrapper.WrapperLookup lookup);

	private JsonElement convert(Identifier id, T value, DynamicOps<JsonElement> ops) {
		DataResult<JsonElement> dataResult = this.codec.encodeStart(ops, value);
		return dataResult
				.mapError(message -> "Invalid entry %s: %s".formatted(id, message))
				.getOrThrow();
	}

	private CompletableFuture<?> write(DataWriter writer, Map<Identifier, JsonElement> entries) {
		return CompletableFuture.allOf(entries.entrySet().stream().map(entry -> {
			Path path = this.pathResolver.resolveJson(entry.getKey());
			return DataProvider.writeToPath(writer, entry.getValue(), path);
		}).toArray(CompletableFuture[]::new));
	}
}
