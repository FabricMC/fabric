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

package net.fabricmc.fabric.mixin.networking;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.impl.networking.CustomPayloadC2SPacketAccessor;

@Mixin(CustomPayloadC2SPacket.class)
public class MixinCustomPayloadC2SPacket implements CustomPayloadC2SPacketAccessor {
	@Shadow
	private Identifier channel;
	@Shadow
	private PacketByteBuf data;

	@Override
	public Identifier getChannel() {
		return channel;
	}

	@Override
	public PacketByteBuf getData() {
		return new PacketByteBuf(this.data.copy());
	}
}
