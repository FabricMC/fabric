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

package net.fabricmc.fabric.mixin.network.client;

import net.fabricmc.fabric.api.event.network.client.S2CPlayConnectCallback;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements ClientPlayPacketListener {

	@Shadow @Final private ClientConnection connection;

	@Inject(method = "onGameJoin", at = @At("RETURN"))
	private void onJoin(GameJoinS2CPacket gameJoinS2CPacket_1, CallbackInfo ci){
		S2CPlayConnectCallback.EVENT.invoker().onJoin(this.connection, this);
	}
}
