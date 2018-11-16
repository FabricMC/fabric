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

package net.fabricmc.fabric.registry;

import com.google.common.collect.ImmutableSet;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.networking.CustomPayloadHandlerRegistry;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.EntityPlayerServer;
import net.minecraft.nbt.TagCompound;
import net.minecraft.network.packet.client.CPacketCustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.IdRegistry;
import net.minecraft.util.registry.ModifiableRegistry;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class RegistrySyncManager {
	public static final Identifier ID = new Identifier("fabric", "registry/sync");
	private static final Set<Identifier> REGISTRY_BLACKLIST = ImmutableSet.of();
	private static final Set<Identifier> REGISTRY_BLACKLIST_NETWORK = ImmutableSet.of();

	private RegistrySyncManager() {

	}

	public static CPacketCustomPayload createPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVarInt(1);
		buf.writeTagCompound(toTag(true));

		CPacketCustomPayload packet = new CPacketCustomPayload(ID, buf);
		return packet;
	}

	public static void receivePacket(PacketContext context, PacketByteBuf buf) {
		int version = buf.readVarInt();
		if (version != 1) {
			// TODO: log error
		}

		TagCompound compound = buf.readTagCompound();
		try {
			apply(compound);
		} catch (RemapException e) {
			// TODO: log error properly
			e.printStackTrace();
		}
	}

	public static TagCompound toTag(boolean isClientSync) {
		TagCompound mainTag = new TagCompound();

		for (Identifier registryId : Registry.REGISTRIES.keys()) {
			if (REGISTRY_BLACKLIST.contains(registryId)) {
				continue;
			} else if (isClientSync && REGISTRY_BLACKLIST_NETWORK.contains(registryId)) {
				continue;
			}

			ModifiableRegistry registry = Registry.REGISTRIES.get(registryId);
			if (registry instanceof IdRegistry && registry instanceof RemappableRegistry) {
				TagCompound registryTag = new TagCompound();
				//noinspection unchecked
				for (Identifier identifier : (Set<Identifier>) registry.keys()) {
					registryTag.setInt(identifier.toString(), registry.getRawId(registry.get(identifier)));
				}
				mainTag.setTag(registryId.toString(), registryTag);
			}
		}

		return mainTag;
	}

	public static void apply(TagCompound mainTag) throws RemapException {
		for (Identifier registryId : Registry.REGISTRIES.keys()) {
			if (!mainTag.hasKey(registryId.toString())) {
				continue;
			}

			TagCompound registryTag = mainTag.getTagCompound(registryId.toString());
			ModifiableRegistry registry = Registry.REGISTRIES.get(registryId);
			if (registry instanceof IdRegistry && registry instanceof RemappableRegistry) {
				Object2IntMap<Identifier> idMap = new Object2IntOpenHashMap<>();
				for (String key : registryTag.getKeys()) {
					idMap.put(new Identifier(key), registryTag.getInt(key));
				}
				((RemappableRegistry) registry).remap(idMap);
			}
		}
	}
}
