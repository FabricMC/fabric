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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.registry.Registry;

// Modify how the trident entity data is sent, changing it from the uuid to the Registry ID for the item
@Mixin(PersistentProjectileEntity.class)
public class TridentEntityMixin {
	@Inject(method = "createSpawnPacket", at = @At("HEAD"), cancellable = true)
	public void sendTridentTypeToClient(CallbackInfoReturnable<Packet<?>> cir) {
		if ((PersistentProjectileEntity) (Object) this instanceof TridentEntity) {
			cir.setReturnValue(new EntitySpawnS2CPacket((PersistentProjectileEntity) (Object) this, Registry.ITEM.getRawId(((TridentEntityAccessor) this).getTridentStack().getItem())));
		}
	}
}
