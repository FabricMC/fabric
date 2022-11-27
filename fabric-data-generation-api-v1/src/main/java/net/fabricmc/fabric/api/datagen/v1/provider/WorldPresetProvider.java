package net.fabricmc.fabric.api.datagen.v1.provider;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public abstract class WorldPresetProvider implements DataProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger("WorldPresetProvider");

	private final DataOutput.PathResolver pathResolver;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
	private final Map<RegistryKey<WorldPreset>, WorldPreset> presets = Maps.newHashMap();

	public WorldPresetProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "worldgen/world_preset");
		this.registriesFuture = registriesFuture;
	}

	/**
	 * Implement this method to register world presets to generate.
	 *
	 * <p>Use one of {@link WorldPresetProvider#addPreset} methods.
	 */
	public abstract void generate(RegistryWrapper.WrapperLookup lookup);

	protected final void addPreset(RegistryKey<WorldPreset> key, WorldPreset preset) {
		Objects.requireNonNull(key, "Preset key cannot be null");
		Objects.requireNonNull(preset, "World preset cannot be null");
		presets.put(key, preset);
	}

	protected final void addPreset(Identifier id, WorldPreset preset) {
		Objects.requireNonNull(id, "Preset id cannot be null");
		Objects.requireNonNull(preset, "World preset cannot be null");
		presets.put(RegistryKey.of(RegistryKeys.WORLD_PRESET, id), preset);
	}

	@Override
	public final CompletableFuture<?> run(DataWriter writer) {
		return this.registriesFuture.thenCompose(lookup -> {
			ArrayList<CompletableFuture<?>> list = new ArrayList<>();
			this.generate(lookup);
			this.presets.forEach((key, preset) -> {
				Identifier id = key.getValue();
				JsonElement json = WorldPreset.CODEC.encodeStart(RegistryOps.of(JsonOps.INSTANCE, lookup), preset).getOrThrow(false, LOGGER::error);
				list.add(DataProvider.writeToPath(writer, json, this.pathResolver.resolveJson(id)));
			});
			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		});
	}

	@Override
	public String getName() {
		return "WorldPresets";
	}
}
