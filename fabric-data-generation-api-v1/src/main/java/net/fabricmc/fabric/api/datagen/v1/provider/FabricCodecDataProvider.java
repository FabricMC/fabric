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
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

/**
 * Extend this class and implement {@link FabricCodecDataProvider#configure}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class FabricCodecDataProvider<T> implements DataProvider {
	private final DataOutput.PathResolver pathResolver;
	private final Codec<T> codec;

	protected FabricCodecDataProvider(FabricDataOutput dataOutput, DataOutput.OutputType outputType, String directoryName, Codec<T> codec) {
		this.pathResolver = dataOutput.getResolver(outputType, directoryName);
		this.codec = codec;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
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
	 */
	protected abstract void configure(BiConsumer<Identifier, T> provider);

	private JsonElement convert(Identifier id, T value) {
		DataResult<JsonElement> dataResult = this.codec.encodeStart(JsonOps.INSTANCE, value);
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
