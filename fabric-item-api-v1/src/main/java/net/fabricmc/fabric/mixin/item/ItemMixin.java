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

package net.fabricmc.fabric.mixin.item;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(Item.class)
abstract class ItemMixin implements ItemExtensions {
	@Unique
	private EquipmentSlotProvider equipmentSlotProvider;

	@Unique
	private CustomDamageHandler customDamageHandler;

	@Unique
	private SoundEvent strongHitSound;
	@Unique
	private SoundEvent weakHitSound;
	@Unique
	private SoundEvent criticalHitSound;
	@Unique
	private SoundEvent knockbackHitSound;
	@Unique
	private SoundEvent noDamageHitSound;
	@Unique
	private SoundEvent sweepingHitSound;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(Item.Settings settings, CallbackInfo info) {
		FabricItemInternals.onBuild(settings, (Item) (Object) this);
	}

	@Override
	public EquipmentSlotProvider fabric_getEquipmentSlotProvider() {
		return equipmentSlotProvider;
	}

	@Override
	public void fabric_setEquipmentSlotProvider(EquipmentSlotProvider equipmentSlotProvider) {
		this.equipmentSlotProvider = equipmentSlotProvider;
	}

	@Override
	public CustomDamageHandler fabric_getCustomDamageHandler() {
		return customDamageHandler;
	}

	@Override
	public void fabric_setCustomDamageHandler(CustomDamageHandler handler) {
		this.customDamageHandler = handler;
	}

	@Override
	public @Nullable SoundEvent fabric_getCriticalHitSound() {
		// TODO Auto-generated method stub
		return criticalHitSound;
	}

	@Override
	public @Nullable SoundEvent fabric_getKnockBackHitSound() {
		// TODO Auto-generated method stub
		return knockbackHitSound;
	}

	@Override
	public @Nullable SoundEvent fabric_getNoDamageHitSound() {
		// TODO Auto-generated method stub
		return noDamageHitSound;
	}

	@Override
	public @Nullable SoundEvent fabric_getStrongHitSound() {
		// TODO Auto-generated method stub
		return strongHitSound;
	}

	@Override
	public @Nullable SoundEvent fabric_getSweepingHitSound() {
		// TODO Auto-generated method stub
		return sweepingHitSound;
	}

	@Override
	public @Nullable SoundEvent fabric_getWeakHitSound() {
		// TODO Auto-generated method stub
		return weakHitSound;
	}

	@Override
	public void fabric_setCriticalHitSound(SoundEvent soundEvent) {
		this.criticalHitSound = soundEvent;

	}

	@Override
	public void fabric_setKnockBackHitSound(SoundEvent soundEvent) {
		this.knockbackHitSound = soundEvent;

	}

	@Override
	public void fabric_setNoDamageHitSound(SoundEvent soundEvent) {
		this.noDamageHitSound = soundEvent;

	}

	@Override
	public void fabric_setStrongHitSound(SoundEvent soundEvent) {
		this.strongHitSound = soundEvent;

	}

	@Override
	public void fabric_setSweepingHitSound(SoundEvent soundEvent) {
		this.sweepingHitSound = soundEvent;

	}

	@Override
	public void fabric_setWeakHitSound(SoundEvent soundEvent) {
		this.weakHitSound = soundEvent;

	}
}
