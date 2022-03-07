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

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;

@Mixin(BucketItem.class)
public class MixinBucketItem {
	@Shadow
	@Final
	private Fluid fluid;

	@Inject(method = "playEmptyingSound", at = @At("HEAD"), cancellable = true)
	private void playEmptyingSound(PlayerEntity player, WorldAccess world, BlockPos pos, CallbackInfo ci) {
		if (fluid instanceof FabricFlowableFluid fabricFlowableFluid) {
			Optional<SoundEvent> emptySound = fabricFlowableFluid.getFabricBucketEmptySound();

			if (emptySound.isPresent()) {
				world.playSound(player, pos, emptySound.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
				world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos);
			}

			ci.cancel();
		}
	}
}
