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
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.zip.Deflater;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistrySyncPacket;
import net.fabricmc.fabric.impl.registry.sync.packet.NbtRegistrySyncPacket;
import net.fabricmc.fabric.impl.registry.sync.packet.RegistrySyncPacket;

public final class RegistrySyncManager {
	static final boolean DEBUG = Boolean.getBoolean("fabric.registry.debug");
	private static final Logger LOGGER = LogManager.getLogger("FabricRegistrySync");
	private static final boolean DEBUG_WRITE_REGISTRY_DATA = Boolean.getBoolean("fabric.registry.debug.writeContentsAsCsv");
	private static final boolean FORCE_NBT_SYNC = Boolean.getBoolean("fabric.registry.forceNbtSync");

	//Set to true after vanilla's bootstrap has completed
	public static boolean postBootstrap = false;

	private RegistrySyncManager() { }

	public static void sendPacket(MinecraftServer server, ServerPlayerEntity player) {
		if (!DEBUG && server.isHost(player.getGameProfile())) {
			return;
		}

		if (FORCE_NBT_SYNC) {
			LOGGER.warn("Force NBT sync is enabled");
			sendPacket(player, NbtRegistrySyncPacket.getInstance());
			return;
		}

		if (ServerPlayNetworking.canSend(player, DirectRegistrySyncPacket.ID)) {
			sendPacket(player, DirectRegistrySyncPacket.getInstance());
		} else {
			LOGGER.warn("Player {} can't receive direct packet, using nbt packet instead", player.getEntityName());
			sendPacket(player, NbtRegistrySyncPacket.getInstance());
		}
	}

	private static void sendPacket(ServerPlayerEntity player, RegistrySyncPacket packet) {
		Map<Identifier, Object2IntMap<Identifier>> map = RegistrySyncManager.createAndPopulateRegistryMap(true, null);

		if (map != null) {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			packet.writeBuffer(buf, map);
			ServerPlayNetworking.send(player, packet.getPacketId(), buf);
		}
	}

	public static void receivePacket(ThreadExecutor<?> executor, RegistrySyncPacket packet, PacketByteBuf buf, boolean accept, Consumer<Exception> errorHandler) {
		if (DEBUG) {
			LOGGER.info("{} raw size: {}", packet.getClass().getSimpleName(), buf.readableBytes());

			// see PacketDeflater
			final byte[] deflateBuffer = new byte[8192];
			ByteBuf byteBuf = buf.copy();
			Deflater deflater = new Deflater();

			int i = byteBuf.readableBytes();
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());

			if (i < 256) {
				packetByteBuf.writeVarInt(0);
				packetByteBuf.writeBytes(byteBuf);
			} else {
				byte[] bs = new byte[i];
				byteBuf.readBytes(bs);
				packetByteBuf.writeVarInt(bs.length);
				deflater.setInput(bs, 0, i);
				deflater.finish();

				while (!deflater.finished()) {
					int j = deflater.deflate(deflateBuffer);
					packetByteBuf.writeBytes(deflateBuffer, 0, j);
				}

				deflater.reset();
			}

			LOGGER.info("{} deflated size: {}", packet.getClass().getSimpleName(), packetByteBuf.readableBytes());
		}

		Map<Identifier, Object2IntMap<Identifier>> map = packet.readBuffer(buf);

		if (accept) {
			try {
				executor.submit(() -> {
					if (map == null) {
						errorHandler.accept(new RemapException("Received null map in sync packet!"));
						return null;
					}

					try {
						apply(map, RemappableRegistry.RemapMode.REMOTE);
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

	/**
	 * Creates a {@link NbtCompound} used to save or sync the registry ids.
	 *
	 * @param isClientSync true when syncing to the client, false when saving
	 * @param activeMap    contains the registry ids that were previously read and applied, can be null.
	 * @return a {@link NbtCompound} to save or sync, null when empty
	 */
	@Nullable
	public static Map<Identifier, Object2IntMap<Identifier>> createAndPopulateRegistryMap(boolean isClientSync, @Nullable Map<Identifier, Object2IntMap<Identifier>> activeMap) {
		Map<Identifier, Object2IntMap<Identifier>> map = new LinkedHashMap<>();

		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			Registry registry = Registry.REGISTRIES.get(registryId);

			if (DEBUG_WRITE_REGISTRY_DATA) {
				File location = new File(".fabric" + File.separatorChar + "debug" + File.separatorChar + "registry");
				boolean c = true;

				if (!location.exists()) {
					if (!location.mkdirs()) {
						LOGGER.warn("[fabric-registry-sync debug] Could not create " + location.getAbsolutePath() + " directory!");
						c = false;
					}
				}

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

			/*
			 * This contains the previous state's registry data, this is used for a few things:
			 * Such as ensuring that previously modded registries or registry entries are not lost or overwritten.
			 */
			Object2IntMap<Identifier> previousIdMap = null;

			if (activeMap != null && activeMap.containsKey(registryId)) {
				previousIdMap = activeMap.get(registryId);
			}

			RegistryAttributeHolder attributeHolder = RegistryAttributeHolder.get(registry);

			if (!attributeHolder.hasAttribute(isClientSync ? RegistryAttribute.SYNCED : RegistryAttribute.PERSISTED)) {
				LOGGER.debug("Not {} registry: {}", isClientSync ? "syncing" : "saving", registryId);
				continue;
			}

			/*
			 * Dont do anything with vanilla registries on client sync.
			 * When saving skip none modded registries that doesnt have previous registry data
			 *
			 * This will not sync IDs if a world has been previously modded, either from removed mods
			 * or a previous version of fabric registry sync, but will save these ids to disk in case the mod or mods
			 * are added back.
			 */
			if ((previousIdMap == null || isClientSync) && !attributeHolder.hasAttribute(RegistryAttribute.MODDED)) {
				LOGGER.debug("Skipping un-modded registry: " + registryId);
				continue;
			} else if (previousIdMap != null) {
				LOGGER.debug("Preserving previously modded registry: " + registryId);
			}

			if (isClientSync) {
				LOGGER.debug("Syncing registry: " + registryId);
			} else {
				LOGGER.debug("Saving registry: " + registryId);
			}

			if (registry instanceof RemappableRegistry) {
				Object2IntMap<Identifier> idMap = new Object2IntLinkedOpenHashMap<>();
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

					idMap.put(id, rawId);
				}

				/*
				 * Look for existing registry key/values that are not in the current registries.
				 * This can happen when registry entries are removed, preventing that ID from being re-used by something else.
				 */
				if (!isClientSync && previousIdMap != null) {
					for (Identifier key : previousIdMap.keySet()) {
						if (!idMap.containsKey(key)) {
							LOGGER.debug("Saving orphaned registry entry: " + key);
							idMap.put(key, previousIdMap.getInt(key));
						}
					}
				}

				map.put(registryId, idMap);
			}
		}

		// Ensure any orphaned registry's are kept on disk
		if (!isClientSync && activeMap != null) {
			for (Identifier registryKey : activeMap.keySet()) {
				if (!map.containsKey(registryKey)) {
					LOGGER.debug("Saving orphaned registry: " + registryKey);
					map.put(registryKey, activeMap.get(registryKey));
				}
			}
		}

		if (map.isEmpty()) {
			return null;
		}

		return map;
	}

	public static void apply(Map<Identifier, Object2IntMap<Identifier>> map, RemappableRegistry.RemapMode mode) throws RemapException {
		Set<Identifier> containedRegistries = Sets.newHashSet(map.keySet());

		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			if (!containedRegistries.remove(registryId)) {
				continue;
			}

			Object2IntMap<Identifier> registryMap = map.get(registryId);
			Registry registry = Registry.REGISTRIES.get(registryId);

			RegistryAttributeHolder attributeHolder = RegistryAttributeHolder.get(registry);

			if (!attributeHolder.hasAttribute(RegistryAttribute.MODDED)) {
				LOGGER.debug("Not applying registry data to vanilla registry {}", registryId.toString());
				continue;
			}

			if (registry instanceof RemappableRegistry) {
				Object2IntMap<Identifier> idMap = new Object2IntOpenHashMap<>();

				for (Identifier key : registryMap.keySet()) {
					idMap.put(key, registryMap.getInt(key));
				}

				((RemappableRegistry) registry).remap(registryId.toString(), idMap, mode);
			}
		}

		if (!containedRegistries.isEmpty()) {
			LOGGER.warn("[fabric-registry-sync] Could not find the following registries: " + Joiner.on(", ").join(containedRegistries));
		}
	}

	public static void unmap() throws RemapException {
		for (Identifier registryId : Registry.REGISTRIES.getIds()) {
			Registry registry = Registry.REGISTRIES.get(registryId);

			if (registry instanceof RemappableRegistry) {
				((RemappableRegistry) registry).unmap(registryId.toString());
			}
		}
	}

	public static void bootstrapRegistries() {
		postBootstrap = true;
	}
}
