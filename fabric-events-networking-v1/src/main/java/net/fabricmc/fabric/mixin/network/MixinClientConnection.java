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

import io.netty.channel.Channel;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.network.NetworkConnectionCallback;
import net.fabricmc.fabric.impl.network.ConnectionEvents;
import net.fabricmc.fabric.impl.network.ConnectionType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection {

	@Shadow	private Channel channel;
	@Shadow	private PacketListener packetListener;

	@Inject(method = "disconnect", at = @At("RETURN"))
	private void onDisconnect(Component component_1, CallbackInfo ci) {
		if (this.channel.isOpen()) {
			ConnectionType type = ConnectionType.getLeaveFrom(this.packetListener);
			Event<NetworkConnectionCallback> event = ConnectionEvents.getConnectionEvent(type);
			if (event != null) {
				event.invoker().onConnection((ClientConnection) (Object) this);
			}
		}
	}

}
