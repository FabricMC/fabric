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

package net.fabricmc.fabric.mixin.fluid.client;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundCategory;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;
import net.fabricmc.fabric.api.util.SoundParameters;
import net.fabricmc.fabric.impl.fluid.FabricFluidClientPlayerEntity;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;
import net.fabricmc.fabric.impl.fluid.UnderfluidSoundLoop;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements FabricFluidClientPlayerEntity {
	//region INTERNAL METHODS AND VARIABLES PLACEHOLDERS

	@Shadow
	@Final
	protected MinecraftClient client;
	@Unique
	private UnderfluidSoundLoop underfluidSound = null;

	//endregion

	//region FAST SWIMMING

	@Redirect(method = "isWalking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
	private boolean isSubmergedInWaterRedirect1(ClientPlayerEntity entity) {
		//Adds the fabric fluids to the valid walkable fluids
		return entity.isSubmergedInWater() || ((FabricFluidEntity) entity).isSubmergedInFabricFluid();
	}

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
	private boolean isSubmergedInWaterRedirect2(ClientPlayerEntity entity) {
		//Adds the fabric fluids to the valid fluids for fast swimming
		return ((FabricFluidEntity) entity).isSubmergedInSwimmableFluid();
	}

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect(ClientPlayerEntity entity) {
		//Adds the fabric fluids to the valid fluids for fast swimming
		return ((FabricFluidEntity) entity).isTouchingSwimmableFluid();
	}

	//endregion

	//region ENTER AND EXIT FLUID SOUNDS

	@Override
	public void enterInFluid(@NotNull FluidState fluidState) {
		//Plays the "enter in fluid" sound and submerged ambient sound
		playEnterInFluidSound(fluidState.getFluid());
		startUnderfluidSound(fluidState.getFluid());
	}

	@Override
	public void exitFromFluid(@NotNull FluidState fluidState) {
		//Stops the current submerged ambient sound
		//Water automatically stops its sound, so there's no need to manage it
		stopUnderfluidSound();

		//Plays the "exit from fluid" sound
		playExitFromFluidSound(fluidState.getFluid());
	}

	@Override
	public void changedFluid(@NotNull FluidState oldFluidState, @NotNull FluidState newFluidState) {
		//Stops the current submerged ambient sound
		//Water automatically stops its sound, so there's no need to manage it
		stopUnderfluidSound();

		//Plays the "exit from fluid" sound
		playExitFromFluidSound(oldFluidState.getFluid());

		//Plays the "enter in fluid" sound and submerged ambient sound
		playEnterInFluidSound(newFluidState.getFluid());
		startUnderfluidSound(newFluidState.getFluid());
	}

	@Unique
	private void playEnterInFluidSound(@NotNull Fluid fluid) {
		if (fluid instanceof FabricFlowableFluid fabricFluid) {
			//Gets the "enter in fluid" sound and plays it
			SoundParameters enterSound = fabricFluid.getEnterSound(getThis().world, getThis());
			enterSound.ifHasSound(sound -> getThis().world.playSound(getThis().getX(), getThis().getY(), getThis().getZ(),
					sound.getSoundEvent(), SoundCategory.AMBIENT, sound.getVolume(), sound.getPitch(), false));
		}
	}

	@Unique
	private void playExitFromFluidSound(@NotNull Fluid fluid) {
		if (fluid instanceof FabricFlowableFluid fabricFluid) {
			//Gets the "exit from fluid" sound and plays it
			SoundParameters exitSound = fabricFluid.getExitSound(getThis().world, getThis());
			exitSound.ifHasSound(sound -> getThis().world.playSound(getThis().getX(), getThis().getY(), getThis().getZ(),
					sound.getSoundEvent(), SoundCategory.AMBIENT, sound.getVolume(), sound.getPitch(), false));
		}
	}

	@Unique
	private void startUnderfluidSound(@NotNull Fluid fluid) {
		if (fluid instanceof FabricFlowableFluid fabricFluid) {
			//Gets the submerged ambient sound and plays it
			SoundParameters subSound = fabricFluid.getSubmergedAmbientSound(getThis().world, getThis());
			subSound.ifHasSound(sound -> underfluidSound = UnderfluidSoundLoop.of(getThis(), sound));

			if (underfluidSound != null) {
				this.client.getSoundManager().play(underfluidSound);
			}
		}
	}

	@Unique
	private void stopUnderfluidSound() {
		//Stops the current submerged ambient sound
		if (underfluidSound != null) {
			underfluidSound.stop();
			underfluidSound = null;
		}
	}

	//endregion

	@Unique
	private ClientPlayerEntity getThis() {
		return (ClientPlayerEntity) (Object) this;
	}
}
