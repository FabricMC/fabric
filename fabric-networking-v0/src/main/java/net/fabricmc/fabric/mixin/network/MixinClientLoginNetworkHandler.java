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

package net.fabricmc.fabric.mixin.network;

import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.network.login.ClientLoginQueryResponseRegistry;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {
	@Shadow
	private Consumer<Text> statusConsumer;
	@Shadow
	private ClientConnection connection;

	@Inject(at = @At("HEAD"), method = "onQueryRequest", cancellable = true)
	public void onQueryRequest(LoginQueryRequestS2CPacket packet, CallbackInfo info) {
		//noinspection ConstantConditions
		ClientLoginNetworkHandler self = (ClientLoginNetworkHandler) (Object) this;
		Optional<LoginQueryResponseC2SPacket> responseOptional = ClientLoginQueryResponseRegistry.INSTANCE.respond(self, connection, packet);
		responseOptional.ifPresent((response) -> {
			this.statusConsumer.accept(new TranslatableText("connect.negotiating"));
			this.connection.send(response);
			info.cancel();
		});
	}
}
