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

package net.fabricmc.fabric.mixin.entity.damage;

import net.fabricmc.fabric.api.event.block.FallDeathSuffixCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DamageTracker.class)
public abstract class MixinDamageTracker {

    @Shadow
    private String fallDeathSuffix;

    @Shadow @Final private LivingEntity entity;

    @Inject(method = "setFallDeathSuffix", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", shift = At.Shift.AFTER), cancellable = true)
    public void setFallDeathSuffix(CallbackInfo ci) {

        final BlockState block = entity.world.getBlockState(new BlockPos(entity.getX(), entity.getBoundingBox().getMin(Direction.Axis.Y), entity.getZ()));
		String suffix = FallDeathSuffixCallback.event.invoker().getFallDeathSuffix(entity, block).suffix;

        if (suffix != null) {
        	fallDeathSuffix = suffix;
			ci.cancel();
		}
    }
}
