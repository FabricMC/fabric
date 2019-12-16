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

package net.fabricmc.fabric.mixin.networking.play;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.event.ServerPlayNetworkHandlerCallback;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPlayPacketContext;
import net.fabricmc.fabric.impl.networking.hook.PlayNetworkHandlerHook;
import net.fabricmc.fabric.impl.networking.handler.AbstractPlayPacketHandler;
import net.fabricmc.fabric.impl.networking.handler.ServerPlayPacketHandler;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements PlayNetworkHandlerHook, ServerPlayPacketContext {
	@Shadow
	@Final
	private MinecraftServer server;
	@Shadow
	@Final
	public ClientConnection client;
	@Shadow public ServerPlayerEntity player;
	// actual fields
	private AbstractPlayPacketHandler<ServerPlayPacketContext> sender;

	@Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("TAIL"))
	private void fabric_onConstructor(CallbackInfo ci) {
		sender = new ServerPlayPacketHandler((ServerPlayNetworkHandler) (Object) this);
		sender.init();
	}

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void fabric_onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo info) {
		// Intentionally async;
		CustomPayloadC2SPacketAccessor accessor = (CustomPayloadC2SPacketAccessor) packet;
		Identifier channel = accessor.getChannel();

		if (sender.accept(channel, this, accessor.getData())) {
			// the original buf is already released as getData doesn't copy!
			info.cancel();
		}
	}

	@Inject(method = "onDisconnected(Lnet/minecraft/text/Text;)V", at = @At("TAIL"))
	private void fabric_injectDisconnect(Text text, CallbackInfo ci) {
		ServerPlayNetworkHandlerCallback.DISCONNECTED.invoker().handle((ServerPlayNetworkHandler) (Object) this);
	}

	@Override
	public MinecraftServer getEngine() {
		return server;
	}

	@Override
	public ServerPlayNetworkHandler getNetworkHandler() {
		return (ServerPlayNetworkHandler) (Object) this;
	}

	@Override
	public AbstractPlayPacketHandler<?> getPacketSender() {
		return sender;
	}

	@Override
	public ServerPlayerEntity getPlayer() {
		return player;
	}
}
