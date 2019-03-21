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
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "changeDimension", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = "ldc=reposition", shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void changeDimension(DimensionType targetType, CallbackInfoReturnable<Entity> cir, MinecraftServer server, DimensionType fromType, ServerWorld fromWorld, ServerWorld toWorld) {
        fromWorld.getProfiler().push("fabric:reposition");
        AttemptDimensionTeleportCallback.EventResult result = AttemptDimensionTeleportCallback.EVENT.invoker().placeEntity((Entity) (Object) this, fromType, targetType);
        if(result == AttemptDimensionTeleportCallback.EventResult.CANCEL_TELEPORT) {
            cir.setReturnValue((Entity) (Object) this);
            fromWorld.getProfiler().pop(); // fabric:reposition
            fromWorld.getProfiler().pop(); // reposition
        }
        else {
            boolean flag = false;
            if(result == AttemptDimensionTeleportCallback.EventResult.DEFAULT && targetType instanceof FabricDimensionType) {
                ((FabricDimensionType) targetType).getEntityPlacer().placeEntity((Entity) (Object) this, targetType, toWorld);
                flag = true;
            }
            if(flag || result == AttemptDimensionTeleportCallback.EventResult.SKIP_FURTHER_PROCESSING) { // sync entity state, just as vanilla does; reason: we don't have conditional mixin wrappers (yet)
                fromWorld.getProfiler().swap("fabric:reloading"); // fabric:reposition
                Entity entity = ((Entity) (Object) this).getType().create(toWorld);
                if(entity != null) {
                    entity.method_5878((Entity) (Object) this);
                    entity.setPositionAndAngles(new BlockPos((Entity) (Object) this), entity.yaw, entity.pitch);
                    boolean teleporting = entity.teleporting;
                    entity.teleporting = true;
                    toWorld.method_18214(entity);
                    entity.teleporting = teleporting;
                    toWorld.method_18769(entity);
                }
                ((Entity) (Object) this).invalid = true;
                fromWorld.getProfiler().pop(); // fabric:reloading
                fromWorld.resetIdleTimeout();
                toWorld.resetIdleTimeout();
                cir.setReturnValue(entity);
                fromWorld.getProfiler().pop(); // reposition
            }
        }
    }
}
