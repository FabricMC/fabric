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
import net.fabricmc.fabric.api.event.network.S2CPacketTypeCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.mixin.networking.MixinClientPlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class ClientSidePacketRegistryImpl extends PacketRegistryImpl implements ClientSidePacketRegistry {
	private final Collection<Identifier> serverPayloadIds = new HashSet<>();

	public static void invalidateRegisteredIdList() {
		((ClientSidePacketRegistryImpl) ClientSidePacketRegistry.INSTANCE).serverPayloadIds.clear();
	}

	@Override
	public boolean canServerReceive(Identifier id) {
		return serverPayloadIds.contains(id);
	}

	@Override
	public void sendToServer(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
		if (handler != null) {
			if (completionListener == null) {
				// stay closer to the vanilla codepath
				handler.sendPacket(packet);
			} else {
				handler.getClientConnection().sendPacket(packet, completionListener);
			}
		} else {
			// TODO: log warning
		}
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return new CustomPayloadC2SPacket(id, buf);
	}

	@Override
	protected void onRegister(Identifier id) {
		ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
		if (handler != null) {
			handler.sendPacket(createRegisterTypePacket(PacketTypes.REGISTER, Collections.singleton(id)));
		}
	}

	@Override
	protected void onUnregister(Identifier id) {
		ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
		if (handler != null) {
			handler.sendPacket(createRegisterTypePacket(PacketTypes.UNREGISTER, Collections.singleton(id)));
		}
	}

	@Override
	protected Collection<Identifier> getIdCollectionFor(PacketContext context) {
		return serverPayloadIds;
	}

	@Override
	protected void onReceivedRegisterPacket(PacketContext context, Collection<Identifier> ids) {
		S2CPacketTypeCallback.REGISTERED.invoker().accept(ids);
	}

	@Override
	protected void onReceivedUnregisterPacket(PacketContext context, Collection<Identifier> ids) {
		S2CPacketTypeCallback.UNREGISTERED.invoker().accept(ids);
	}

	public final boolean accept(CustomPayloadS2CPacket packet, PacketContext context) {
		return accept(packet.getChannel(), context, packet.getData());
	}
}
