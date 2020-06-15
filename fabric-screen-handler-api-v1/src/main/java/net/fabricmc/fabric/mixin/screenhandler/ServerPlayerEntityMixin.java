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

package net.fabricmc.fabric.mixin.screenhandler;

import java.util.OptionalInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.network.Packet;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.fabricmc.fabric.impl.screenhandler.Networking;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
	@Shadow
	private int screenHandlerSyncId;

	@Unique
	private final ThreadLocal<ScreenHandler> fabric_openedScreenHandler = new ThreadLocal<>();

	@Inject(method = "openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void fabric_storeOpenedScreenHandler(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> info, ScreenHandler handler) {
		if (factory instanceof ExtendedScreenHandlerFactory) {
			fabric_openedScreenHandler.set(handler);
		} else if (handler.getType() instanceof ExtendedScreenHandlerType<?>) {
			Identifier id = Registry.SCREEN_HANDLER.getId(handler.getType());
			throw new IllegalArgumentException("[Fabric] Extended screen handler " + id + " must be opened with an ExtendedScreenHandlerFactory!");
		}
	}

	@Redirect(method = "openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
	private void fabric_replaceVanillaScreenPacket(ServerPlayNetworkHandler networkHandler, Packet<?> packet, NamedScreenHandlerFactory factory) {
		if (factory instanceof ExtendedScreenHandlerFactory) {
			ScreenHandler handler = fabric_openedScreenHandler.get();

			if (handler.getType() instanceof ExtendedScreenHandlerType<?>) {
				Networking.sendOpenPacket((ServerPlayerEntity) (Object) this, (ExtendedScreenHandlerFactory) factory, handler, screenHandlerSyncId);
			} else {
				Identifier id = Registry.SCREEN_HANDLER.getId(handler.getType());
				throw new IllegalArgumentException("[Fabric] Non-extended screen handler " + id + " must not be opened with an ExtendedScreenHandlerFactory!");
			}
		} else {
			// Use vanilla logic for non-extended screen handlers
			networkHandler.sendPacket(packet);
		}
	}

	@Inject(method = "openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;", at = @At("RETURN"))
	private void fabric_clearStoredScreenHandler(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> info) {
		fabric_openedScreenHandler.remove();
	}
}
