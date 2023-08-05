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
import net.minecraft.network.packet.c2s.login.LoginQueryResponse;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufLoginQueryResponse;

@Mixin(LoginQueryResponseC2SPacket.class)
public class LoginQueryResponseC2SPacketMixin {
	@Inject(method = "readResponse", at = @At("HEAD"), cancellable = true)
	private static void readResponse(int queryId, PacketByteBuf buf, CallbackInfoReturnable<LoginQueryResponse> cir) {
		PacketByteBuf copy = PacketByteBufs.copy(buf);
		buf.skipBytes(buf.readableBytes());
		cir.setReturnValue(new PacketByteBufLoginQueryResponse(copy));
	}
}
