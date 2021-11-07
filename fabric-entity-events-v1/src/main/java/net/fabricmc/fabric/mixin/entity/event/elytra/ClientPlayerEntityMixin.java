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

package net.fabricmc.fabric.mixin.entity.event.elytra;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import net.fabricmc.fabric.api.entity.event.v1.AllowElytraFlight;

@Mixin(value = ClientPlayerEntity.class, priority = 900)
abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
	ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
		throw new AssertionError();
	}

	/**
	 * Allow the client to start flight with custom elytras implemented through {@link AllowElytraFlight}.
	 */
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"), method = "tickMovement", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isClimbing()Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;checkFallFlying()Z")), require = 1, allow = 1)
	void injectElytraStart(CallbackInfo info) {
		ClientPlayerEntity self = ClientPlayerEntity.class.cast(this);

		if (AllowElytraFlight.EVENT.invoker().allowElytraFlight(this, false) && this.checkFallFlying()) {
			self.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
		}
	}
}
