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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.v1.event.ClientPlayNetworkHandlerCallback;
import net.fabricmc.fabric.api.networking.v1.receiver.ClientPlayPacketContext;
import net.fabricmc.fabric.impl.networking.handler.AbstractPlayPacketHandler;
import net.fabricmc.fabric.impl.networking.handler.ClientPlayPacketHandler;
import net.fabricmc.fabric.impl.networking.hook.PlayNetworkHandlerHook;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements PlayNetworkHandlerHook, ClientPlayPacketContext {
	@Shadow
	private MinecraftClient client;
	@Shadow private ClientWorld world;
	// actual fields
	private AbstractPlayPacketHandler<ClientPlayPacketContext> sender;

	@Inject(method = "<init>*", at = @At("TAIL"))
	private void fabric_onConstructor(CallbackInfo ci) {
		sender = new ClientPlayPacketHandler((ClientPlayNetworkHandler) (Object) this);
		// at this point, the handler is just waiting for a server game join packet;
		// server still has the login network handler, so don't send anything yet!
	}

	@Inject(method = "onGameJoin", at = @At("TAIL"))
	private void fabric_onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		sender.init();
	}

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void fabric_onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo info) {
		if (sender.accept(packet.getChannel(), this, ((CustomPayloadS2CPacketAccessor) packet).getRawBuffer())) {
			info.cancel();
		}
	}

	@Inject(method = "onDisconnected(Lnet/minecraft/text/Text;)V", at = @At("TAIL"))
	private void fabric_injectDisconnect(Text text, CallbackInfo ci) {
		ClientPlayNetworkHandlerCallback.DISCONNECTED.invoker().handle((ClientPlayNetworkHandler) (Object) this);
	}

	@Override
	public ClientPlayNetworkHandler getNetworkHandler() {
		return (ClientPlayNetworkHandler) (Object) this;
	}

	@Override
	public MinecraftClient getEngine() {
		return client;
	}

	@Override
	public AbstractPlayPacketHandler<?> getPacketSender() {
		return sender;
	}

	@Override
	public ClientPlayerEntity getPlayer() {
		return client.player;
	}
}
