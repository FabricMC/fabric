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

package net.fabricmc.fabric.impl.networking;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.network.C2SPacketTypeCallback;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class ServerSidePacketRegistryImpl extends PacketRegistryImpl implements ServerSidePacketRegistry {
	private final WeakHashMap<PlayerEntity, Collection<Identifier>> playerPayloadIds = new WeakHashMap<>();
	private final Set<WeakReference<ServerPlayNetworkHandler>> handlers = new HashSet<>();

	public void onQueryResponse(LoginQueryResponseC2SPacket packet) {
	}

	public void addNetworkHandler(ServerPlayNetworkHandler handler) {
		handlers.add(new WeakReference<>(handler));
	}

	protected void forEachHandler(Consumer<ServerPlayNetworkHandler> consumer) {
		for (Iterator<WeakReference<ServerPlayNetworkHandler>> iter = handlers.iterator(); iter.hasNext();) {
			ServerPlayNetworkHandler server = iter.next().get();

			if (server != null) {
				consumer.accept(server);
			} else {
				iter.remove();
			}
		}
	}

	@Override
	public boolean canPlayerReceive(PlayerEntity player, Identifier id) {
		Collection<Identifier> ids = playerPayloadIds.get(player);
		return ids != null && ids.contains(id);
	}

	@Override
	public void sendToPlayer(PlayerEntity player, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) player).networkHandler.sendPacket(packet, completionListener);
		} else {
			throw new UnsupportedOperationException("Can only send to ServerPlayerEntities!");
		}
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return new CustomPayloadS2CPacket(id, buf);
	}

	@Override
	protected void onRegister(Identifier id) {
		createRegisterTypePacket(PacketTypes.REGISTER, Collections.singleton(id))
				.ifPresent((packet) -> forEachHandler((n) -> n.sendPacket(packet)));
	}

	@Override
	protected void onUnregister(Identifier id) {
		createRegisterTypePacket(PacketTypes.UNREGISTER, Collections.singleton(id))
				.ifPresent((packet) -> forEachHandler((n) -> n.sendPacket(packet)));
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
}
