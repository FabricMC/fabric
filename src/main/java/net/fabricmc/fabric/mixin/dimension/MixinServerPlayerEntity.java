/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.dimension;

import net.fabricmc.fabric.api.dimension.FabricDimensionType;
import net.fabricmc.fabric.api.event.dimension.AttemptDimensionTeleportCallback;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity {

    @Shadow
    @Final
    public MinecraftServer server;
    @Shadow private int field_13978;
    @Shadow private float field_13997;
    @Shadow private int field_13979;

    @Inject(method = "changeDimension", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = "ldc=placing", shift = At.Shift.AFTER), cancellable = true)
    private void changeDimension(DimensionType targetType, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        DimensionType fromType = player.dimension;
        AttemptDimensionTeleportCallback.EventResult result = AttemptDimensionTeleportCallback.EVENT.invoker().placeEntity(player, fromType, targetType);
        if(result == AttemptDimensionTeleportCallback.EventResult.CANCEL_TELEPORT) {
            cir.setReturnValue(player);
        }
        else {
            boolean flag = false;
            //noinspection ConstantConditions
            ServerWorld toWorld = player.getServer().getWorld(targetType);
            if(targetType instanceof FabricDimensionType) {
                ((FabricDimensionType) targetType).getEntityPlacer().placeEntity(player, targetType, toWorld);
                flag = true;
            }
            if(flag || result == AttemptDimensionTeleportCallback.EventResult.SKIP_FURTHER_PROCESSING) {
                player.setPositionAndAngles(player.x, player.y, player.z, player.yaw, player.pitch); // sync the position to the client
                player.setVelocity(Vec3d.ZERO);
                // below code mostly copied from vanilla, since we don't have conditional mixin wrappers (yet)
                PlayerManager playerManager = this.server.getPlayerManager();
                ServerWorld oldWorld = player.getServerWorld();
                oldWorld.getProfiler().pop();
                player.setWorld(toWorld);
                toWorld.method_18211(player);
                this.method_18783(oldWorld); // TODO this method is for handling the dimension travel advancements, but needs a mixin to not mess up modded dimensions
                player.networkHandler.teleportRequest(player.x, player.y, player.z, player.yaw, player.pitch);
                player.interactionManager.setWorld(toWorld);
                player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket((player).abilities));
                playerManager.method_14606(player, toWorld);
                playerManager.method_14594(player);
                for(StatusEffectInstance statusEffectInstance_1 : (player).getPotionEffects()) {
                    (player).networkHandler.sendPacket(new EntityPotionEffectS2CPacket((player).getEntityId(), statusEffectInstance_1));
                }
                (player).networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
                this.field_13978 = -1;
                this.field_13997 = -1.0F;
                this.field_13979 = -1;
                cir.setReturnValue(player);
            }
        }
    }

    @Shadow
    protected abstract void method_18783(ServerWorld serverWorld_1);
}
