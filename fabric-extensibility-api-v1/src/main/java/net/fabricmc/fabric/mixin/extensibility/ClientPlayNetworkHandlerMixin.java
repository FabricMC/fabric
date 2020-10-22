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

package net.fabricmc.fabric.mixin.extensibility;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.fabric.impl.extensibility.item.v1.ExtensibilityApiEntry;
import net.fabricmc.fabric.impl.extensibility.item.v1.FabricTridentItemEntity;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Shadow
	MinecraftClient client;

	@Shadow
	ClientWorld world;

	@Inject(method = "onEntitySpawn", at = @At("HEAD"), cancellable = true)
	public void onEntitySpawnMixin(EntitySpawnS2CPacket packet, CallbackInfo info) {
		NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler) (Object) this, (ThreadExecutor<?>) client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		EntityType<?> entityType = packet.getEntityTypeId();
		Entity entity15 = null;
		Entity entity16;

		if (entityType == EntityType.TRIDENT) {
			entity15 = new TridentEntity(world, d, e, f);
			entity16 = world.getEntityById(packet.getEntityData());

			ItemStack tridentStack = new ItemStack(ExtensibilityApiEntry.tridentItems.remove());

			if (tridentStack.getItem() != Items.TRIDENT) {
				entity15 = new FabricTridentItemEntity(world, d, e, f, tridentStack);
			}

			if (entity16 != null) {
				((PersistentProjectileEntity) entity15).setOwner(entity16);
			}
		}

		if (entity15 != null) {
			int i = packet.getId();
			entity15.updateTrackedPosition(d, e, f);
			entity15.refreshPositionAfterTeleport(d, e, f);
			entity15.pitch = packet.getPitch() * 360 / 256.0F;
			entity15.yaw = packet.getYaw() * 360 / 256.0F;
			entity15.setEntityId(i);
			entity15.setUuid(packet.getUuid());
			world.addEntity(i, entity15);
			info.cancel();
		}
	}
}
