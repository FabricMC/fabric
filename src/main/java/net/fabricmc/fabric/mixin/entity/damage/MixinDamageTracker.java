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

    @Inject(method = "setFallDeathSuffix", at = @At(value = "JUMP", ordinal = 3, shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    public void setFallDeathSuffix(CallbackInfo ci, final Block block) {

        if (block instanceof Climbable) {
            String suffix = ((Climbable) block).getDeathSuffix();
            if (suffix != null) {
                fallDeathSuffix = suffix;
            }
            else {
               fallDeathSuffix = "ladder";
            }
        }
    }
}
