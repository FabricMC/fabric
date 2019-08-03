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

import net.fabricmc.fabric.impl.network.login.S2CLoginQueryQueueImpl;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public class MixinServerLoginNetworkHandler {
	@Unique
	private S2CLoginQueryQueueImpl loginQueue;
	@Shadow
	public ClientConnection client;

	@Inject(at = @At("HEAD"), method = "acceptPlayer()V", cancellable = true)
	private void cancelAcceptIfAwaitingResponse(CallbackInfo info) {
		if (loginQueue == null) {
			//noinspection ConstantConditions
			ServerLoginNetworkHandler self = (ServerLoginNetworkHandler) (Object) this;
			loginQueue = new S2CLoginQueryQueueImpl(self);
		}

		if (loginQueue.tick()) {
			info.cancel();
		}
	}


	@Inject(at = @At("HEAD"), method = "onQueryResponse", cancellable = true)
	public void onQueryResponse(LoginQueryResponseC2SPacket packet, CallbackInfo info) {
		if (loginQueue.receiveResponse(packet)) {
			info.cancel();
		}
	}
}
