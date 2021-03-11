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

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.item.v1.CustomItemSettingType;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;

public final class FabricItemInternals {
	public static final CustomItemSettingType<EquipmentSlotProvider> EQUIPMENT_SLOT_PROVIDER = CustomItemSettingType.of(() -> null);
	public static final CustomItemSettingType<CustomDamageHandler> CUSTOM_DAMAGE_HANDLER = CustomItemSettingType.of(() -> null);

	private static final Map<Item.Settings, Map<CustomItemSettingType<?>, Object>> customSettings = new WeakHashMap<>();

	private FabricItemInternals() {
	}

	public static <T> void setCustomSetting(Item.Settings settings, CustomItemSettingType<T> settingType, T settingValue) {
		customSettings.computeIfAbsent(settings, s -> new WeakHashMap<>()).put(settingType, settingValue);
	}

	public static <T> T getSetting(Item item, CustomItemSettingType<T> settingType) {
		return ((ItemExtensions) item).fabric_getCustomItemSetting(settingType);
	}

	public static void onBuild(Item.Settings settings, ItemExtensions item) {
		Map<CustomItemSettingType<?>, Object> customItemSettings = item.fabric_getCustomItemSettings();
		customItemSettings.putAll(customSettings.getOrDefault(settings, Collections.emptyMap()));
	}
}
