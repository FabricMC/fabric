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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.google.common.base.Joiner;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public final class RegistrySyncManager {
	public static final boolean DEBUG = Boolean.getBoolean("fabric.registry.debug");
	static final Identifier ID = new Identifier("fabric", "registry/sync");
	private static final Logger LOGGER = LogManager.getLogger();
	public static final boolean DEBUG_WRITE_REGISTRY_DATA = Boolean.getBoolean("fabric.registry.debug.writeContentsAsCsv");

	private RegistrySyncManager() { }

	public static Packet<?> createPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(0); // Header, reserved for future use
		RegistrySerialization.toBuf(buf, true);

		return ServerSidePacketRegistry.INSTANCE.toPacket(ID, buf);
	}

	public static void receivePacket(PacketContext context, PacketByteBuf buf, boolean accept, Consumer<Exception> errorHandler) {
		if (buf.readInt() != 0) {
			errorHandler.accept(new RemapException("Invalid packet header"));
		}

		Map<Identifier, Object2IntMap<Identifier>> registryIdMaps = RegistrySerialization.fromBuf(buf);

		if (accept) {
			try {
				context.getTaskQueue().submit(() -> {
					try {
						apply(registryIdMaps, RemappableRegistry.RemapMode.REMOTE);
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

	private static <T> void validateRegistry(Registry<T> registry, Identifier registryId) {
		IntSet rawIdsFound = new IntOpenHashSet();

		for (T o : registry) {
			Identifier id = registry.getId(o);

			if (id == null) {
				LOGGER.error("[fabric-registry-sync] Inconsistency detected in " + registryId + ": object " + o + " has no string ID!");
				continue;
			}

			int rawId = registry.getRawId(o);

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
	}

	public static void validateRegistries() {
		for (MutableRegistry<?> registry : Registry.REGISTRIES) {
			validateRegistry(registry, Registry.REGISTRIES.getId(registry));
		}
	}

	private static <T> void dumpRegistryDataCSV(Registry<T> registry, OutputStream stream) throws IOException {
		StringBuilder builder = new StringBuilder("Raw ID,String ID,Class Type\n");

		for (T o : registry) {
			String classType = (o == null) ? "null" : o.getClass().getName();
			Identifier id = registry.getId(o);
			if (id == null) continue;

			int rawId = registry.getRawId(o);
			String stringId = id.toString();
			builder.append("\"").append(rawId).append("\",\"").append(stringId).append("\",\"").append(classType).append("\"\n");
		}

		stream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
	}

	public static void dumpAllRegistryData() {
		File location = new File(".fabric" + File.separatorChar + "debug" + File.separatorChar + "registry");

		if (!location.exists() && !location.mkdirs()) {
			LOGGER.warn("[fabric-registry-sync debug] Could not create " + location.getAbsolutePath() + " directory!");
			return;
		}

		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			MutableRegistry<?> registry = Registry.REGISTRIES.get(registryId);

			if (registry != null) {
				File file = new File(location, registryId.toString().replace(':', '.').replace('/', '.') + ".csv");

				try (FileOutputStream stream = new FileOutputStream(file)) {
					dumpRegistryDataCSV(registry, stream);
				} catch (IOException e) {
					LOGGER.warn("[fabric-registry-sync debug] Could not write to " + file.getAbsolutePath() + "!", e);
				}
			}
		}
	}

	public static void apply(Map<Identifier, Object2IntMap<Identifier>> registryIdMaps, RemappableRegistry.RemapMode mode) throws RemapException {
		Set<Identifier> missingRegistries = new HashSet<>(registryIdMaps.keySet());

		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			if (!missingRegistries.remove(registryId)) {
				continue;
			}

			Object2IntMap<Identifier> registryIdMap = registryIdMaps.get(registryId);
			MutableRegistry registry = Registry.REGISTRIES.get(registryId);

			if (registry instanceof RemappableRegistry) {
				((RemappableRegistry) registry).remap(registryId.toString(), registryIdMap, mode);
			}
		}

		if (!missingRegistries.isEmpty()) {
			LOGGER.warn("[fabric-registry-sync] Could not find the following registries: " + Joiner.on(", ").join(missingRegistries));
		}
	}

	public static void unmap() throws RemapException {
		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			MutableRegistry registry = Registry.REGISTRIES.get(registryId);

			if (registry instanceof RemappableRegistry) {
				((RemappableRegistry) registry).unmap(registryId.toString());
			}
		}
	}
}
