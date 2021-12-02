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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;

@Mixin(BucketItem.class)
public class BucketItemMixin {
	@Unique
	private Fluid drainedFluid = Fluids.EMPTY;

	@Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FluidDrainable;tryDrainFluid(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/fluid/Fluid;"))
	private Fluid tryDrainFluidRedirect(FluidDrainable fluidDrainable, WorldAccess world, BlockPos blockPos, BlockState blockState) {
		//Obtain the drained fluid
		drainedFluid = fluidDrainable.tryDrainFluid(world, blockPos, blockState);
		return drainedFluid;
	}

	@Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
	private void playSoundRedirect(PlayerEntity player, SoundEvent sound, float volume, float pitch) {
		//Plays the sound from the drained fluid
		if (drainedFluid instanceof FabricFlowableFluid) {
			FabricFlowableFluid fFluid = (FabricFlowableFluid) drainedFluid;
			fFluid.getBucketFillSound().ifPresent(fillSound -> player.playSound(fillSound, 1.0f, 1.0f));
		} else {
			player.playSound(sound, volume, pitch);
		}
	}
}
