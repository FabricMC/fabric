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
		CompoundTag biomeRegistryData = null;

		if (existingTag != null) {
			biomeRegistryData = existingTag.getCompound(Registry.BIOME_KEY.getValue().toString());
		}

		MutableRegistry<?> registry = dynamicRegistryManager.get(Registry.BIOME_KEY);
		CompoundTag biomeIdMap = remapRegistry(Registry.BIOME_KEY.getValue(), registry, biomeRegistryData);
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
	private static <T> CompoundTag remapRegistry(Identifier registryId, MutableRegistry<T> registry, @Nullable CompoundTag existingTag) throws RemapException {
		if (!(registry instanceof RemappableRegistry)) {
			throw new UnsupportedOperationException("Cannot remap un re-mappable registry");
		}

		boolean isModded = registry.getIds().stream().anyMatch(id -> !id.getNamespace().equals("minecraft"));

		// The current registry might not be modded, but we might have previous changed vanilla ids that we should try and remap
		if (existingTag != null && !isModded) {
			isModded = existingTag.getKeys().stream()
					.map(existingTag::getString)
					.map(Identifier::new)
					.anyMatch(id -> !id.getNamespace().equals("minecraft"));
		}

		if (LOGGER.isDebugEnabled()) {
			if (existingTag == null) {
				LOGGER.debug("No existing data found, assuming new registry with {} entries. modded = {}", registry.getIds().size(), isModded);
			} else {
				LOGGER.debug("Existing registry data found. modded = {}", isModded);

				for (T entry : registry) {
					//noinspection unchecked
					Identifier id = registry.getId(entry);
					int rawId = registry.getRawId(entry);

					if (id == null) continue;

					if (existingTag.getKeys().contains(id.toString())) {
						int existingRawId = existingTag.getInt(id.toString());

						if (rawId != existingRawId) {
							LOGGER.debug("Remapping {} {} -> {}", id.toString(), rawId, existingRawId);
						} else {
							LOGGER.debug("Using existing id for {} {}", id.toString(), rawId);
						}
					} else {
						LOGGER.debug("Found new registry entry {}", id.toString());
					}
				}
			}
		}

		// If we have some existing ids and the registry contains modded/datapack entries we remap the registry with those
		if (existingTag != null && isModded) {
			LOGGER.debug("Remapping {} with {} entries", registryId, registry.getIds().size());
			Object2IntMap<Identifier> idMap = new Object2IntOpenHashMap<>();

			for (String key : existingTag.getKeys()) {
				idMap.put(new Identifier(key), existingTag.getInt(key));
			}

			((RemappableRegistry) registry).remap(registryId.toString(), idMap, RemappableRegistry.RemapMode.AUTHORITATIVE);
		} else {
			LOGGER.debug("Skipping remap of {}", registryId);
		}

		// Now start to build up what we are going to save out
		CompoundTag registryTag = new CompoundTag();

		// Save all ids as they appear in the remapped, or new registry to disk even if not modded.
		for (T entry : registry) {
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
