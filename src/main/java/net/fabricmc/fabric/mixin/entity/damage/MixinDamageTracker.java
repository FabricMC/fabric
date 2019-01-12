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

import net.fabricmc.fabric.block.Climbable;
import net.minecraft.block.Block;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DamageTracker.class)
abstract public class MixinDamageTracker {

    @Shadow
    private String fallDeathSuffix;

    @Inject(method = "setFallDeathSuffix", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void setFallDeathSuffix(CallbackInfo ci) {
        //To avoid casting to damage tracker multiple times.
        final DamageTracker thisTracker = ((DamageTracker) (Object) this);

        final Block block = thisTracker.getEntity().world.getBlockState(new BlockPos(thisTracker.getEntity().x, thisTracker.getEntity().getBoundingBox().minY, thisTracker.getEntity().z)).getBlock();

        if (block instanceof Climbable) {
            String suffix = ((Climbable) block).getFallDeathSuffix();
            if (suffix != null) {
                fallDeathSuffix = suffix;
            }
            else {
               fallDeathSuffix = "ladder";
            }

            ci.cancel();
        }
    }
}
