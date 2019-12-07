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

package net.fabricmc.fabric.impl.networking.receiver;

import java.util.Collections;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.receiver.ClientPlayPacketContext;
import net.fabricmc.fabric.impl.networking.PacketHelper;

final class ClientPlayPacketReceiverRegistry extends NotifyingPacketReceiverRegistry<ClientPlayPacketContext> {
	@Override
	protected void notify(boolean register, Identifier channel) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;

		PacketByteBuf buf = PacketHelper.createRegisterChannelBuf(Collections.singleton(channel));
		player.networkHandler.sendPacket(new CustomPayloadC2SPacket(register ? PacketHelper.REGISTER : PacketHelper.UNREGISTER, buf));
	}
}
