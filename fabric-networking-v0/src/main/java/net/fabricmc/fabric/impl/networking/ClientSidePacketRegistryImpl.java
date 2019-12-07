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

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.receiver.PlayPacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiverRegistry;
import net.fabricmc.fabric.api.networking.v1.receiver.ClientPacketReceiverRegistries;
import net.fabricmc.fabric.api.networking.v1.sender.PacketSenders;

public class ClientSidePacketRegistryImpl extends PacketRegistryImpl implements ClientSidePacketRegistry {
	@Override
	public boolean canServerReceive(Identifier id) {
		return PacketSenders.of(MinecraftClient.getInstance().player.networkHandler).accepts(id);
	}

	@Override
	PacketReceiverRegistry<? extends PlayPacketContext> getReceiverRegistry() {
		return ClientPacketReceiverRegistries.PLAY;
	}

	@Override
	public void sendToServer(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

		if (handler != null) {
			if (completionListener == null) {
				// stay closer to the vanilla codepath
				handler.sendPacket(packet);
			} else {
				handler.getConnection().send(packet, completionListener);
			}
		} else {
			LOGGER.warn("Sending packet " + packet + " to server failed, not connected!");
		}
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return new CustomPayloadC2SPacket(id, buf);
	}
}
