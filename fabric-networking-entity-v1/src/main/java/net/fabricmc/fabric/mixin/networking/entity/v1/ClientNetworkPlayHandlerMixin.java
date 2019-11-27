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

package net.fabricmc.fabric.mixin.networking.entity.v1;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.networking.entity.v1.FabricEntityNetworkingSettings;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientNetworkPlayHandlerMixin {
	@Inject(method = "onEntitySpawn", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onEntitySpawnPacket(EntitySpawnS2CPacket packet, CallbackInfo ci, double x, double y, double z, EntityType<?> type, Entity localEntity) {
		if (FabricEntityNetworkingSettings.WARN_INVALID_VANILLA_PACKET && localEntity == null) {
			FabricEntityNetworkingSettings.LOGGER.warn("Received ignored vanilla spawn entity packet for entity type \"{}\"", Registry.ENTITY_TYPE.getId(type));
		}
	}
}
