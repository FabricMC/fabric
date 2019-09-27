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

package net.fabricmc.fabric.mixin.networkingentity;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Unique
    private EntitySpawnS2CPacket packet;

    public MixinClientPlayNetworkHandler() {
    }

    @Inject(
        method = {"onEntitySpawn"},
        at = {@At("HEAD")}
    )
    public void onEntitySpawnPacket(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        this.packet = packet;
    }

    @ModifyVariable(
        method = {"onEntitySpawn"},
        at = @At(value = "STORE"),
        name = "entity_40"
    )
    public Entity onEntitySpawn(Entity prevEntity) {
        Entity entity = packet.getEntityTypeId().create(((ClientPlayNetworkHandler) (Object) this).getWorld());
		entity.setPosition(packet.getX(), packet.getY(), packet.getZ());

        return entity;
    }
}
