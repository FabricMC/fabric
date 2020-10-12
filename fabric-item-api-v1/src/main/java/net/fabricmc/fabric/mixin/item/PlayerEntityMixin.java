/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.item;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;

import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Unique
	private boolean muteSound = false;

	/**
	 * Captures all of the {@code World#playSound(PlayerEntity, double, double, double, net.minecraft.sound.SoundEvent, net.minecraft.sound.SoundCategory, float, float)}
	 * and replaces the volume argument with 0 to mute it if muteSound has been set.
	 * @param volume
	 * @return
	 */
	@ModifyArg(at = @At(value = "INVOKE", target = "net/minecraft/world/World.playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), index = 6, method = "attack")
	public float muteSoundEvent(float volume) {
		if (muteSound) {
			muteSound = false;
			return 0.0F;
		}

		return volume;
	}

	@Inject(at = @At(value = "FIELD", target = "net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK:Lnet/minecraft/sound/SoundEvent;", opcode = Opcodes.GETSTATIC), method = "attack", cancellable = false)
	public void onPlayKnockbackSound(CallbackInfo ci) {
		PlayerEntity pe = (PlayerEntity) (Object) this;
		ItemExtensions item = ((ItemExtensions) pe.getMainHandStack().getItem());

		if (item.fabric_getKnockBackHitSound() != null) {
			this.muteSound = true;
			item.fabric_getKnockBackHitSound().playSound(pe.world, pe);
		}
	}

	@Inject(at = @At(value = "FIELD", target = "net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP:Lnet/minecraft/sound/SoundEvent;", opcode = Opcodes.GETSTATIC), method = "attack", cancellable = false)
	public void onPlaySweepSound(CallbackInfo ci) {
		PlayerEntity pe = (PlayerEntity) (Object) this;
		ItemExtensions item = ((ItemExtensions) pe.getMainHandStack().getItem());

		if (item.fabric_getSweepingHitSound() != null) {
			this.muteSound = true;
			item.fabric_getSweepingHitSound().playSound(pe.world, pe);
		}
	}

	@Inject(at = @At(value = "FIELD", target = "net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_CRIT:Lnet/minecraft/sound/SoundEvent;", opcode = Opcodes.GETSTATIC), method = "attack", cancellable = false)
	public void onPlayCritSound(CallbackInfo ci) {
		PlayerEntity pe = (PlayerEntity) (Object) this;
		ItemExtensions item = ((ItemExtensions) pe.getMainHandStack().getItem());

		if (item.fabric_getCriticalHitSound() != null) {
			this.muteSound = true;
			item.fabric_getCriticalHitSound().playSound(pe.world, pe);
		}
	}

	@Inject(at = @At(value = "FIELD", target = "net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_STRONG:Lnet/minecraft/sound/SoundEvent;", opcode = Opcodes.GETSTATIC), method = "attack", cancellable = false)
	public void onPlayStrongSound(CallbackInfo ci) {
		PlayerEntity pe = (PlayerEntity) (Object) this;
		ItemExtensions item = ((ItemExtensions) pe.getMainHandStack().getItem());

		if (item.fabric_getStrongHitSound() != null) {
			this.muteSound = true;
			item.fabric_getStrongHitSound().playSound(pe.world, pe);
		}
	}

	@Inject(at = @At(value = "FIELD", target = "net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_WEAK:Lnet/minecraft/sound/SoundEvent;", opcode = Opcodes.GETSTATIC), method = "attack", cancellable = false)
	public void onPlayWeakSound(CallbackInfo ci) {
		PlayerEntity pe = (PlayerEntity) (Object) this;
		ItemExtensions item = ((ItemExtensions) pe.getMainHandStack().getItem());

		if (item.fabric_getWeakHitSound() != null) {
			this.muteSound = true;
			item.fabric_getWeakHitSound().playSound(pe.world, pe);
		}
	}

	@Inject(at = @At(value = "FIELD", target = "net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE:Lnet/minecraft/sound/SoundEvent;", opcode = Opcodes.GETSTATIC), method = "attack", cancellable = false)
	public void onPlayNoDamageSound(CallbackInfo ci) {
		PlayerEntity pe = (PlayerEntity) (Object) this;
		ItemExtensions item = ((ItemExtensions) pe.getMainHandStack().getItem());

		if (item.fabric_getNoDamageHitSound() != null) {
			this.muteSound = true;
			item.fabric_getNoDamageHitSound().playSound(pe.world, pe);
		}
	}
}
