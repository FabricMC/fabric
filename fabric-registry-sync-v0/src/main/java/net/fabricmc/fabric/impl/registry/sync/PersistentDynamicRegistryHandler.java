package net.fabricmc.fabric.impl.registry.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class PersistentDynamicRegistryHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void remapDynamicRegistries(DynamicRegistryManager.Impl dynamicRegistryManager, Path saveDir) {
		LOGGER.debug("Starting registry remap");

		CompoundTag registryData;

		try {
			registryData = remapDynamicRegistries(dynamicRegistryManager, readCompoundTag(getDataPath(saveDir)));
		} catch (RemapException | IOException e) {
			// TODO try the backups here?
			throw new RuntimeException(e);
		}

		writeCompoundTag(registryData, getDataPath(saveDir));
	}

	@NotNull
	private static CompoundTag remapDynamicRegistries(DynamicRegistryManager.Impl dynamicRegistryManager, @Nullable CompoundTag existingTag) throws RemapException {
		CompoundTag registries = new CompoundTag();

		// For now we only care about biomes, but lets keep our options open
		MutableRegistry<?> registry = dynamicRegistryManager.get(Registry.BIOME_KEY);
		CompoundTag biomeIdMap = remapRegistry(Registry.BIOME_KEY.getValue(), registry, existingTag);
		registries.put(Registry.BIOME_KEY.getValue().toString(), biomeIdMap);

		CompoundTag outputTag = new CompoundTag();
		outputTag.putInt("version", 1);
		outputTag.put("registries", registries);
		return registries;
	}

	/**
	 * Remaps a registry if existing data is passed in.
	 * Then writes out the ids in the registry (remapped or a new world).
	 * Keeps hold of the orphaned registry entries as to not overwrite them.
	 */
	@SuppressWarnings("unchecked")
	private static CompoundTag remapRegistry(Identifier registryId, MutableRegistry registry, @Nullable CompoundTag existingTag) throws RemapException {
		if (!(registry instanceof RemappableRegistry)) {
			throw new UnsupportedOperationException("Cannot remap un re-mappable registry");
		}

		// If we have some existing ids we remap the registry with those
		if (existingTag != null) {
			Object2IntMap<Identifier> idMap = new Object2IntOpenHashMap<>();

			for (String key : existingTag.getKeys()) {
				idMap.put(new Identifier(key), existingTag.getInt(key));
			}

			((RemappableRegistry) registry).remap(registryId.toString(), idMap, RemappableRegistry.RemapMode.AUTHORITATIVE);
		}

		// Now start to build up what we are going to save out
		CompoundTag registryTag = new CompoundTag();

		// Save all ids as they appear in the remapped, or new registry to disk
		for (Object entry : registry) {
			//noinspection unchecked
			Identifier id = registry.getId(entry);

			if (id == null) {
				continue;
			}

			int rawId = registry.getRawId(entry);
			registryTag.putInt(id.toString(), rawId);
		}

		/*
		 * Look for existing registry key/values that are not in the current registries.
		 * This can happen when registry entries are removed, preventing that ID from being re-used by something else.
		 */
		if (existingTag != null) {
			for (String key : existingTag.getKeys()) {
				if (!registryTag.contains(key)) {
					LOGGER.debug("Saving orphaned registry entry: " + key);
					registryTag.putInt(key, registryTag.getInt(key));
				}
			}
		}

		return registryTag;
	}

	private static Path getDataPath(Path saveDir) {
		return saveDir.resolve("data").resolve("fabricDynamicRegistry.dat");
	}

	@Nullable
	private static CompoundTag readCompoundTag(Path path) throws IOException {
		if (!Files.exists(path)) {
			return null;
		}

		try (InputStream inputStream = Files.newInputStream(path)) {
			return NbtIo.readCompressed(inputStream);
		}
	}

	private static void writeCompoundTag(CompoundTag compoundTag, Path path) {
		// TODO handle backups?
		try {
			Files.createDirectories(path.getParent());

			try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE)) {
				NbtIo.writeCompressed(compoundTag, outputStream);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
