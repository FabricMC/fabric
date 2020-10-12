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

package net.fabricmc.fabric.impl.item;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.minecraft.sound.SoundEvent;

public interface ItemExtensions {
	/* @Nullable */ EquipmentSlotProvider fabric_getEquipmentSlotProvider();
	void fabric_setEquipmentSlotProvider(EquipmentSlotProvider equipmentSlotProvider);
	/* @Nullable */ CustomDamageHandler fabric_getCustomDamageHandler();
	void fabric_setCustomDamageHandler(CustomDamageHandler handler);

	@Nullable SoundEvent fabric_getStrongHitSound();
	void fabric_setStrongHitSound(SoundEvent soundEvent);
	@Nullable SoundEvent fabric_getWeakHitSound();
	void fabric_setWeakHitSound(SoundEvent soundEvent);
	@Nullable SoundEvent fabric_getCriticalHitSound();
	void fabric_setCriticalHitSound(SoundEvent soundEvent);
	@Nullable SoundEvent fabric_getKnockBackHitSound();
	void fabric_setKnockBackHitSound(SoundEvent soundEvent);
	@Nullable SoundEvent fabric_getNoDamageHitSound();
	void fabric_setNoDamageHitSound(SoundEvent soundEvent);
	@Nullable SoundEvent fabric_getSweepingHitSound();
	void fabric_setSweepingHitSound(SoundEvent soundEvent);
	
}
