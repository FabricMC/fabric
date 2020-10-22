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

import io.netty.buffer.Unpooled;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

@Mixin(PersistentProjectileEntity.class)
public class TridentEntityMixin {
	@Inject(method = "createSpawnPacket", at = @At("HEAD"))
	public void sendTridentTypeToClient(CallbackInfoReturnable<Packet<?>> cir) {
		if ((Entity) (Object) this instanceof TridentEntity) {
			PacketByteBuf buff = new PacketByteBuf(Unpooled.buffer());
			buff.writeInt(Registry.ITEM.getRawId(((TridentEntity) (Object) this).tridentStack.getItem()));
			buff.writeUuid(((TridentEntity) (Object) this).getUuid());
			((TridentEntity) (Object) this).getEntityWorld().getPlayers().forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new Identifier("fabric-extensibility-api-v1", "custom-trident-info-packet"), buff));
		}
	}
}
