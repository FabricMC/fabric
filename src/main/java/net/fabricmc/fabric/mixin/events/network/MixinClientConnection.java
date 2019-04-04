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

package net.fabricmc.fabric.mixin.events.network;

import net.fabricmc.fabric.api.event.network.DisconnectFromServerCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.text.TextComponent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
	@Shadow
	@Final
	private NetworkSide side;

	@Inject(
		method = "disconnect",
		at = @At(value = "FIELD", target = "Lnet/minecraft/network/ClientConnection;disconnectReason:Lnet/minecraft/text/TextComponent;", opcode = Opcodes.PUTFIELD)
	)
	public void disconnect(TextComponent reason, CallbackInfo callback) {
		DisconnectFromServerCallback.EVENT.invoker().disconnected(reason);
	}
}
