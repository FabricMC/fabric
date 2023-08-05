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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.fabricmc.fabric.impl.networking.payload.PayloadHelper;

@Mixin(CustomPayloadC2SPacket.class)
public class CustomPayloadC2SPacketMixin {
	@Inject(
			method = "readPayload",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/common/CustomPayloadC2SPacket;readUnknownPayload(Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/network/packet/UnknownCustomPayload;"),
			cancellable = true
	)
	private static void readPayload(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomPayload> cir) {
		cir.setReturnValue(new PacketByteBufPayload(id, PayloadHelper.read(buf)));
	}
}
