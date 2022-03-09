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

package net.fabricmc.fabric.mixin.fluid;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.sound.SoundEvent;

@Mixin(BucketItem.class)
public class MixinBucketItem {
	@Shadow
	@Final
	private Fluid fluid;

	@ModifyArg(method = "playEmptyingSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), index = 2)
	private SoundEvent modSoundEvent(SoundEvent value) {
		//Sets the specified value if is present, otherwise does nothing.
		//Lava and water, not overriding this function, maintain their default behaviour.
		return fluid.getFabricBucketEmptySound().orElse(value);
	}
}
