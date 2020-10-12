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
import net.fabricmc.fabric.api.item.v1.*;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(Item.class)
abstract class ItemMixin implements ItemExtensions {
	@Unique
	private EquipmentSlotProvider equipmentSlotProvider;

	@Unique
	private CustomDamageHandler customDamageHandler;

	@Unique
	private SoundPlayer strongHitSound;
	@Unique
	private SoundPlayer weakHitSound;
	@Unique
	private SoundPlayer criticalHitSound;
	@Unique
	private SoundPlayer knockbackHitSound;
	@Unique
	private SoundPlayer noDamageHitSound;
	@Unique
	private SoundPlayer sweepingHitSound;

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
	public @Nullable SoundPlayer fabric_getCriticalHitSound() {
		// TODO Auto-generated method stub
		return criticalHitSound;
	}

	@Override
	public @Nullable SoundPlayer fabric_getKnockBackHitSound() {
		// TODO Auto-generated method stub
		return knockbackHitSound;
	}

	@Override
	public @Nullable SoundPlayer fabric_getNoDamageHitSound() {
		// TODO Auto-generated method stub
		return noDamageHitSound;
	}

	@Override
	public @Nullable SoundPlayer fabric_getStrongHitSound() {
		// TODO Auto-generated method stub
		return strongHitSound;
	}

	@Override
	public @Nullable SoundPlayer fabric_getSweepingHitSound() {
		// TODO Auto-generated method stub
		return sweepingHitSound;
	}

	@Override
	public @Nullable SoundPlayer fabric_getWeakHitSound() {
		// TODO Auto-generated method stub
		return weakHitSound;
	}

	@Override
	public void fabric_setCriticalHitSound(SoundPlayer soundEvent) {
		this.criticalHitSound = soundEvent;

	}

	@Override
	public void fabric_setKnockBackHitSound(SoundPlayer soundEvent) {
		this.knockbackHitSound = soundEvent;

	}

	@Override
	public void fabric_setNoDamageHitSound(SoundPlayer soundEvent) {
		this.noDamageHitSound = soundEvent;

	}

	@Override
	public void fabric_setStrongHitSound(SoundPlayer soundEvent) {
		this.strongHitSound = soundEvent;

	}

	@Override
	public void fabric_setSweepingHitSound(SoundPlayer soundEvent) {
		this.sweepingHitSound = soundEvent;

	}

	@Override
	public void fabric_setWeakHitSound(SoundPlayer soundEvent) {
		this.weakHitSound = soundEvent;

	}
}
