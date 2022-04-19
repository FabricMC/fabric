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

package net.fabricmc.fabric.impl.registry.sync.packet;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.registry.sync.RegistryMapSerializer;

/**
 * A method to sync registry ids using {@link NbtCompound} and {@link PacketByteBuf#writeNbt}.
 * Kept here for old version support.
 */
// TODO: Remove
@Deprecated
public class NbtRegistryPacketHandler extends RegistryPacketHandler {
	private static final Identifier ID = new Identifier("fabric", "registry/sync");

	private Map<Identifier, Object2IntMap<Identifier>> syncedRegistryMap;

	@Override
	public Identifier getPacketId() {
		return ID;
	}

	@Override
	public void sendPacket(ServerPlayerEntity player, Map<Identifier, Object2IntMap<Identifier>> registryMap) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeNbt(RegistryMapSerializer.toNbt(registryMap));
		sendPacket(player, buf);
	}

	@Override
	public void receivePacket(PacketByteBuf buf) {
		computeBufSize(buf);
		NbtCompound nbt = buf.readNbt();
		syncedRegistryMap = nbt != null ? RegistryMapSerializer.fromNbt(nbt) : null;
	}

	@Override
	public int getTotalPacketReceived() {
		return 1;
	}

	@Override
	public boolean isPacketFinished() {
		return true;
	}

	@Override
	@Nullable
	public Map<Identifier, Object2IntMap<Identifier>> getSyncedRegistryMap() {
		return syncedRegistryMap;
	}
}
