package net.fabricmc.fabric.api.datagen.v1.provider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public abstract class FabricDataProvider<T> implements DataProvider {
	private final DataOutput.PathResolver pathResolver;

	protected FabricDataProvider(FabricDataOutput dataOutput, DataOutput.OutputType outputType, String directoryName) {
		this.pathResolver = dataOutput.getResolver(outputType, directoryName);
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

	protected abstract JsonElement convert(Identifier id, T value);
	protected abstract void configure(BiConsumer<Identifier, T> provider);

	private CompletableFuture<?> write(DataWriter writer, Map<Identifier, JsonElement> entries) {
		return CompletableFuture.allOf(entries.entrySet().stream().map(entry -> {
			Path path = this.pathResolver.resolveJson(entry.getKey());
			return DataProvider.writeToPath(writer, entry.getValue(), path);
		}).toArray(CompletableFuture[]::new));
	}
}
