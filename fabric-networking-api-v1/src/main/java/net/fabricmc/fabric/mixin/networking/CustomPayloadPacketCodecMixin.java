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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.networking.CustomPayloadTypeProvider;
import net.fabricmc.fabric.impl.networking.FabricCustomPayloadPacketCodec;

@Mixin(targets = "net/minecraft/network/packet/CustomPayload$1")
public abstract class CustomPayloadPacketCodecMixin<B extends PacketByteBuf> implements PacketCodec<B, CustomPayload>, FabricCustomPayloadPacketCodec<B> {
	@Unique
	private CustomPayloadTypeProvider<B> customPayloadTypeProvider;

	@Override
	public void fabric_setPacketCodecProvider(CustomPayloadTypeProvider<B> customPayloadTypeProvider) {
		if (this.customPayloadTypeProvider != null) {
			throw new IllegalStateException("Payload codec provider is already set!");
		}

		this.customPayloadTypeProvider = customPayloadTypeProvider;
	}

	@WrapOperation(method = {
			"encode(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/packet/CustomPayload;)V",
			"decode(Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/network/packet/CustomPayload;"
	}, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/CustomPayload$1;getCodec(Lnet/minecraft/util/Identifier;)Lnet/minecraft/network/codec/PacketCodec;"))
	private PacketCodec<B, ? extends CustomPayload> wrapGetCodec(@Coerce PacketCodec<B, CustomPayload> instance, Identifier identifier, Operation<PacketCodec<B, CustomPayload>> original, B packetByteBuf) {
		if (customPayloadTypeProvider != null) {
			CustomPayload.Type<B, ? extends CustomPayload> payloadType = customPayloadTypeProvider.get(packetByteBuf, identifier);

			if (payloadType != null) {
				return payloadType.codec();
			}
		}

		return original.call(instance, identifier);
	}
}
