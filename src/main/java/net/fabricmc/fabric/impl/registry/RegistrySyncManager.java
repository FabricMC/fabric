/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.registry;

import com.google.common.collect.ImmutableSet;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.IdRegistry;
import net.minecraft.util.registry.ModifiableRegistry;
import net.minecraft.util.registry.Registry;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class RegistrySyncManager {
	public static final Identifier ID = new Identifier("fabric", "registry/sync");
	private static final Set<Identifier> REGISTRY_BLACKLIST = ImmutableSet.of();
	private static final Set<Identifier> REGISTRY_BLACKLIST_NETWORK = ImmutableSet.of();

	private RegistrySyncManager() {

	}

	public static Packet<?> createPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCompoundTag(toTag(true));

		return ServerSidePacketRegistry.INSTANCE.toPacket(ID, buf);
	}

	public static void receivePacket(PacketContext context, PacketByteBuf buf, boolean accept) {
		CompoundTag compound = buf.readCompoundTag();

		if (accept) {
			try {
				context.getTaskQueue().executeFuture(() -> {
					try {
						apply(compound, false);
					} catch (RemapException e) {
						// TODO: log error properly
						e.printStackTrace();
					}
				}).get(30, TimeUnit.SECONDS);
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException | TimeoutException e) {
				// TODO: better error handling
				new Exception("Failed to apply received packets in time!", e).printStackTrace();
			}
		}
	}

	public static CompoundTag toTag(boolean isClientSync) {
		CompoundTag mainTag = new CompoundTag();

		for (Identifier registryId : Registry.REGISTRIES.keys()) {
			if (REGISTRY_BLACKLIST.contains(registryId)) {
				continue;
			} else if (isClientSync && REGISTRY_BLACKLIST_NETWORK.contains(registryId)) {
				continue;
			}

			ModifiableRegistry registry = Registry.REGISTRIES.get(registryId);
			if (registry instanceof IdRegistry && registry instanceof RemappableRegistry) {
				CompoundTag registryTag = new CompoundTag();
				//noinspection unchecked
				for (Identifier identifier : (Set<Identifier>) registry.keys()) {
					registryTag.putInt(identifier.toString(), registry.getRawId(registry.get(identifier)));
				}
				mainTag.put(registryId.toString(), registryTag);
			}
		}

		CompoundTag tag = new CompoundTag();
		tag.putInt("version", 1);
		tag.put("registries", mainTag);

		return tag;
	}

	public static void apply(CompoundTag tag, boolean reallocateMissingEntries) throws RemapException {
		CompoundTag mainTag = tag.getCompound("registries");

		for (Identifier registryId : Registry.REGISTRIES.keys()) {
			if (!mainTag.containsKey(registryId.toString())) {
				continue;
			}

			CompoundTag registryTag = mainTag.getCompound(registryId.toString());
			ModifiableRegistry registry = Registry.REGISTRIES.get(registryId);
			if (registry instanceof IdRegistry && registry instanceof RemappableRegistry) {
				Object2IntMap<Identifier> idMap = new Object2IntOpenHashMap<>();
				for (String key : registryTag.getKeys()) {
					idMap.put(new Identifier(key), registryTag.getInt(key));
				}
				((RemappableRegistry) registry).remap(idMap, reallocateMissingEntries);
			}
		}
	}

	public static void unmap() throws RemapException {
		for (Identifier registryId : Registry.REGISTRIES.keys()) {
			ModifiableRegistry registry = Registry.REGISTRIES.get(registryId);
			if (registry instanceof RemappableRegistry) {
				((RemappableRegistry) registry).unmap();
			}
		}
	}
}
