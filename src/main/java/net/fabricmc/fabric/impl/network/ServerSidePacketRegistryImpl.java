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

package net.fabricmc.fabric.impl.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.event.network.C2SPacketTypeCallback;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.fabric.impl.accessors.CustomPayloadC2SPacketAccessor;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.WeakHashMap;

public class ServerSidePacketRegistryImpl extends PacketRegistryImpl implements ServerSidePacketRegistry {
	private final WeakHashMap<PlayerEntity, Collection<Identifier>> playerPayloadIds = new WeakHashMap<>();

	public void onQueryResponse(LoginQueryResponseC2SPacket packet) {
	}

	@Override
	public boolean canPlayerReceive(PlayerEntity player, Identifier id) {
		Collection<Identifier> ids = playerPayloadIds.get(player);
		if (ids != null) {
			return ids.contains(id);
		} else {
			return false;
		}
	}

	@Override
	public void sendToPlayer(PlayerEntity player, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		if (!(player instanceof ServerPlayerEntity)) {
			throw new RuntimeException("Can only send to ServerPlayerEntities!");
		} else {
			((ServerPlayerEntity) player).networkHandler.sendPacket(packet, completionListener);
		}
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return new CustomPayloadS2CPacket(id, buf);
	}

	@Override
	protected void onRegister(Identifier id) {
		/* MinecraftServer server = FabricLoader.INSTANCE.getEnvironmentHandler().getServerInstance();
		if (server != null) {
			Packet<?> packet = createRegisterTypePacket(PacketTypes.REGISTER, Collections.singleton(id));
			PlayerStream.all(server).forEach((p) -> sendToPlayer(p, packet));
		} */
		// TODO
	}

	@Override
	protected void onUnregister(Identifier id) {
		/* MinecraftServer server = FabricLoader.INSTANCE.getEnvironmentHandler().getServerInstance();
		if (server != null) {
			Packet<?> packet = createRegisterTypePacket(PacketTypes.UNREGISTER, Collections.singleton(id));
			PlayerStream.all(server).forEach((p) -> sendToPlayer(p, packet));
		} */
		// TODO
	}

	@Override
	protected Collection<Identifier> getIdCollectionFor(PacketContext context) {
		return playerPayloadIds.computeIfAbsent(context.getPlayer(), (p) -> new HashSet<>());
	}

	@Override
	protected void onReceivedRegisterPacket(PacketContext context, Collection<Identifier> ids) {
		C2SPacketTypeCallback.REGISTERED.invoker().accept(context.getPlayer(), ids);
	}

	@Override
	protected void onReceivedUnregisterPacket(PacketContext context, Collection<Identifier> ids) {
		C2SPacketTypeCallback.UNREGISTERED.invoker().accept(context.getPlayer(), ids);
	}

	public final boolean accept(CustomPayloadC2SPacket packet, PacketContext context) {
		CustomPayloadC2SPacketAccessor accessor = ((CustomPayloadC2SPacketAccessor) packet);
		return accept(accessor.getChannel(), context, accessor.getData());
	}
}
