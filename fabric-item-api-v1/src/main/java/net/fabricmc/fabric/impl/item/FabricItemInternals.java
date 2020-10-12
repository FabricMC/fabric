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

import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;

public final class FabricItemInternals {
	private static final WeakHashMap<Item.Settings, ExtraData> extraData = new WeakHashMap<>();

	private FabricItemInternals() {
	}

	public static ExtraData computeExtraData(Item.Settings settings) {
		return extraData.computeIfAbsent(settings, s -> new ExtraData());
	}

	public static void onBuild(Item.Settings settings, Item item) {
		ExtraData data = extraData.get(settings);

		if (data != null) {
			((ItemExtensions) item).fabric_setEquipmentSlotProvider(data.equipmentSlotProvider);
			((ItemExtensions) item).fabric_setCustomDamageHandler(data.customDamageHandler);
			
			((ItemExtensions) item).fabric_setStrongHitSound(data.strongHitSound);
			((ItemExtensions) item).fabric_setWeakHitSound(data.weakHitSound);
			((ItemExtensions) item).fabric_setCriticalHitSound(data.criticalHitSound);
			((ItemExtensions) item).fabric_setKnockBackHitSound(data.knockbackHitSound);
			((ItemExtensions) item).fabric_setNoDamageHitSound(data.noDamageHitSound);
			((ItemExtensions) item).fabric_setSweepingHitSound(data.sweepingHitSound);

		}
	}

	public static final class ExtraData {
		private /* @Nullable */ EquipmentSlotProvider equipmentSlotProvider;
		private /* @Nullable */ CustomDamageHandler customDamageHandler;
		private @Nullable SoundEvent strongHitSound;
		private @Nullable SoundEvent weakHitSound;
		private @Nullable SoundEvent criticalHitSound;
		private @Nullable SoundEvent knockbackHitSound;
		private @Nullable SoundEvent noDamageHitSound;
		private @Nullable SoundEvent sweepingHitSound;

		public void equipmentSlot(EquipmentSlotProvider equipmentSlotProvider) {
			this.equipmentSlotProvider = equipmentSlotProvider;
		}

		public void customDamage(CustomDamageHandler handler) {
			this.customDamageHandler = handler;
		}

		public void strongHitSound(SoundEvent sound) {
			this.strongHitSound = sound;
		}

		public void weakHitSound(SoundEvent sound) {
			this.weakHitSound = sound;
		}

		public void criticalHitSound(SoundEvent sound) {
			this.criticalHitSound = sound;
		}

		public void knockbackHitSound(SoundEvent sound) {
			this.knockbackHitSound = sound;
		}

		public void noDamageHitSound(SoundEvent sound) {
			this.noDamageHitSound = sound;
		}

		public void sweepingHitSound(SoundEvent sound) {
			this.sweepingHitSound = sound;
		}
	}
}
