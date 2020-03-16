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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public final class RegistrySyncManager {
	static final boolean DEBUG = System.getProperty("fabric.registry.debug", "false").equalsIgnoreCase("true");
	static final Identifier ID = new Identifier("fabric", "registry/sync");
	private static final Logger LOGGER = LogManager.getLogger();
	private static final boolean DEBUG_WRITE_REGISTRY_DATA = System.getProperty("fabric.registry.debug.writeContentsAsCsv", "false").equalsIgnoreCase("true");
	private static final RegistryTypes REGISTRY_TYPES = RegistryTypes.getInstance();

	public static boolean postBootstrap = false;

	private RegistrySyncManager() { }

	public static Packet<?> createPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCompoundTag(toTag(true, null));

		return ServerSidePacketRegistry.INSTANCE.toPacket(ID, buf);
	}

	public static void receivePacket(PacketContext context, PacketByteBuf buf, boolean accept, Consumer<Exception> errorHandler) {
		CompoundTag compound = buf.readCompoundTag();

		if (accept) {
			try {
				context.getTaskQueue().submit(() -> {
					if (compound == null) {
						errorHandler.accept(new RemapException("Received null compound tag in sync packet!"));
						return null;
					}

					try {
						apply(compound, RemappableRegistry.RemapMode.REMOTE);
					} catch (RemapException e) {
						errorHandler.accept(e);
					}

					return null;
				}).get(30, TimeUnit.SECONDS);
			} catch (ExecutionException | InterruptedException | TimeoutException e) {
				errorHandler.accept(e);
			}
		}
	}

	public static CompoundTag toTag(boolean isClientSync, CompoundTag activeIdMap) {
		CompoundTag mainTag = new CompoundTag();

		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			if (DEBUG_WRITE_REGISTRY_DATA) {
				File location = new File(".fabric" + File.separatorChar + "debug" + File.separatorChar + "registry");
				boolean c = true;

				if (!location.exists()) {
					if (!location.mkdirs()) {
						LOGGER.warn("[fabric-registry-sync debug] Could not create " + location.getAbsolutePath() + " directory!");
						c = false;
					}
				}

				MutableRegistry registry = Registry.REGISTRIES.get(registryId);

				if (c && registry != null) {
					File file = new File(location, registryId.toString().replace(':', '.').replace('/', '.') + ".csv");

					try (FileOutputStream stream = new FileOutputStream(file)) {
						StringBuilder builder = new StringBuilder("Raw ID,String ID,Class Type\n");

						for (Object o : registry) {
							String classType = (o == null) ? "null" : o.getClass().getName();
							//noinspection unchecked
							Identifier id = registry.getId(o);
							if (id == null) continue;

							//noinspection unchecked
							int rawId = registry.getRawId(o);
							String stringId = id.toString();
							builder.append("\"").append(rawId).append("\",\"").append(stringId).append("\",\"").append(classType).append("\"\n");
						}

						stream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
					} catch (IOException e) {
						LOGGER.warn("[fabric-registry-sync debug] Could not write to " + file.getAbsolutePath() + "!", e);
					}
				}
			}

			CompoundTag existingRegistryData = null;

			if (activeIdMap != null && activeIdMap.contains(registryId.toString())) {
				existingRegistryData = activeIdMap.getCompound(registryId.toString());
			}

			//Dont save none persistent registries
			if (!isClientSync && REGISTRY_TYPES.nonePersistent.contains(registryId)) {
				LOGGER.debug("Not saving registry: " + registryId);
				continue;
			}

			//Dont sync network blacklisted registries
			if (isClientSync && REGISTRY_TYPES.networkBlacklist.contains(registryId)) {
				LOGGER.debug("Not syncing registry: " + registryId);
				continue;
			}

			//Keep vanilla registry that we have no existing registry entries for
			if (existingRegistryData == null && !isRegistryModded(registryId)) {
				LOGGER.debug("Skipping vanilla registry: " + registryId);
				continue;
			} else {
				LOGGER.debug("Syncing vanilla registry: " + registryId);
			}

			MutableRegistry registry = Registry.REGISTRIES.get(registryId);

			if (registry instanceof RemappableRegistry) {
				CompoundTag registryTag = new CompoundTag();
				IntSet rawIdsFound = DEBUG ? new IntOpenHashSet() : null;

				for (Object o : registry) {
					//noinspection unchecked
					Identifier id = registry.getId(o);
					if (id == null) continue;

					//noinspection unchecked
					int rawId = registry.getRawId(o);

					if (DEBUG) {
						if (registry.get(id) != o) {
							LOGGER.error("[fabric-registry-sync] Inconsistency detected in " + registryId + ": object " + o + " -> string ID " + id + " -> object " + registry.get(id) + "!");
						}

						if (registry.get(rawId) != o) {
							LOGGER.error("[fabric-registry-sync] Inconsistency detected in " + registryId + ": object " + o + " -> integer ID " + rawId + " -> object " + registry.get(rawId) + "!");
						}

						if (!rawIdsFound.add(rawId)) {
							LOGGER.error("[fabric-registry-sync] Inconsistency detected in " + registryId + ": multiple objects hold the raw ID " + rawId + " (this one is " + id + ")");
						}
					}

					registryTag.putInt(id.toString(), rawId);
				}

				// Look for existing registry key/values that are not in the current registries
				if (!isClientSync && existingRegistryData != null) {
					for (String key : existingRegistryData.getKeys()) {
						if (!registryTag.contains(key)) {
							LOGGER.info("Saving orphaned registry entry: " + key);
							registryTag.putInt(key, registryTag.getInt(key));
						}
					}
				}

				mainTag.put(registryId.toString(), registryTag);
			}
		}

		// Ensure any orphaned registry's are kept on disk
		if (!isClientSync && activeIdMap != null) {
			for (String registryKey : activeIdMap.getKeys()) {
				if (!mainTag.contains(registryKey)) {
					LOGGER.info("Saving orphaned registry: " + registryKey);
					mainTag.put(registryKey, activeIdMap.getCompound(registryKey));
				}
			}
		}

		CompoundTag tag = new CompoundTag();
		tag.putInt("version", 1);
		tag.put("registries", mainTag);

		return tag;
	}

	public static CompoundTag apply(CompoundTag tag, RemappableRegistry.RemapMode mode) throws RemapException {
		CompoundTag mainTag = tag.getCompound("registries");
		Set<String> containedRegistries = Sets.newHashSet(mainTag.getKeys());

		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			if (!containedRegistries.remove(registryId.toString())) {
				continue;
			}

			CompoundTag registryTag = mainTag.getCompound(registryId.toString());
			MutableRegistry registry = Registry.REGISTRIES.get(registryId);

			if (registry instanceof RemappableRegistry) {
				Object2IntMap<Identifier> idMap = new Object2IntOpenHashMap<>();

				for (String key : registryTag.getKeys()) {
					idMap.put(new Identifier(key), registryTag.getInt(key));
				}

				((RemappableRegistry) registry).remap(registryId.toString(), idMap, mode);
			}
		}

		if (!containedRegistries.isEmpty()) {
			LOGGER.warn("[fabric-registry-sync] Could not find the following registries: " + Joiner.on(", ").join(containedRegistries));
		}

		return mainTag;
	}

	public static void unmap() throws RemapException {
		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			MutableRegistry registry = Registry.REGISTRIES.get(registryId);

			if (registry instanceof RemappableRegistry) {
				((RemappableRegistry) registry).unmap(registryId.toString());
			}
		}
	}

	public static void bootstrapRegistries() {
		for (MutableRegistry<?> registry : Registry.REGISTRIES) {
			if (registry instanceof ModdableRegistry) {
				((ModdableRegistry) registry).storeIdHash(registry.getIds().hashCode());
			}
		}

		postBootstrap = true;
	}

	private static void markModded(Registry<?> registry) {
		if (registry instanceof ModdableRegistry) {
			((ModdableRegistry) registry).markModded();
		} else {
			throw new RuntimeException("Cannot mark a none moddable registry as modded!");
		}
	}

	public static boolean isRegistryModded(Identifier registryId) {
		//All none minecraft registries are modded
		if (!registryId.getNamespace().equals("minecraft")) {
			return true;
		}

		Registry<?> registry = Registry.REGISTRIES.get(registryId);

		if (registry instanceof ModdableRegistry) {
			ModdableRegistry moddableRegistry = (ModdableRegistry) registry;
			return moddableRegistry.isModded();
		} else {
			return false; //TODO what should this be?
		}
	}

	private static class RegistryTypes {
		private List<Identifier> nonePersistent;
		private List<Identifier> persistent;
		private List<Identifier> networkBlacklist;

		private static Gson GSON = new GsonBuilder()
				.registerTypeAdapter(Identifier.class, new TypeAdapter<Identifier>() {
					@Override
					public void write(JsonWriter out, Identifier value) throws IOException {
						out.value(value.toString());
					}

					@Override
					public Identifier read(JsonReader in) throws IOException {
						return new Identifier(in.nextString());
					}
				}).create();

		public static RegistryTypes getInstance() {
			ModContainer modContainer = FabricLoader.getInstance().getModContainer("fabric-registry-sync-v0").get();

			try (InputStreamReader isr = new InputStreamReader(Files.newInputStream(modContainer.getPath("fabric-registry-sync-v0.registry-types.json")))) {
				return GSON.fromJson(isr, RegistryTypes.class);
			} catch (IOException e) {
				throw new RuntimeException("Failed to read registry types", e);
			}
		}
	}
}
