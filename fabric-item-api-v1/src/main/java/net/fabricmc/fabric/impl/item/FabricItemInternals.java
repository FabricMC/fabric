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

import java.util.Map;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.item.v1.CustomItemSetting;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;

public final class FabricItemInternals {
	public static final CustomItemSetting<EquipmentSlotProvider> EQUIPMENT_SLOT_PROVIDER = CustomItemSetting.of(() -> null);
	public static final CustomItemSetting<CustomDamageHandler> CUSTOM_DAMAGE_HANDLER = CustomItemSetting.of(() -> null);

	private FabricItemInternals() {
	}

	public static <T> T getSetting(Item item, CustomItemSetting<T> setting) {
		return ((ItemExtensions) item).fabric_getCustomItemSetting(setting);
	}

	public static void onBuild(Item.Settings settings, ItemExtensions item) {
		if (settings instanceof FabricItemSettings) {
			Map<CustomItemSetting<?>, Object> customItemSettings = item.fabric_getCustomItemSettings();
			customItemSettings.putAll(((FabricItemSettings) settings).getCustomSettings());
		}
	}
}
