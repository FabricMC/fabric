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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.item.v1.CustomItemSetting;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;

public class CustomItemSettingImpl<T> implements CustomItemSetting<T> {
	public static final CustomItemSetting<EquipmentSlotProvider> EQUIPMENT_SLOT_PROVIDER = CustomItemSetting.create(() -> null);
	public static final CustomItemSetting<CustomDamageHandler> CUSTOM_DAMAGE_HANDLER = CustomItemSetting.create(() -> null);

	private static final Map<Item.Settings, Collection<CustomItemSettingImpl<?>>> CUSTOM_SETTINGS = new WeakHashMap<>();

	private final Map<Item.Settings, T> customSettings = new WeakHashMap<>();
	private final Map<Item, T> customItemSettings = new HashMap<>();
	private final Supplier<T> defaultValue;

	public CustomItemSettingImpl(Supplier<T> defaultValue) {
		Objects.requireNonNull(defaultValue);

		this.defaultValue = defaultValue;
	}

	@Override
	public T getValue(Item item) {
		Objects.requireNonNull(item);

		return this.customItemSettings.computeIfAbsent(item, i -> this.defaultValue.get());
	}

	@Override
	public void set(@NotNull Item.Settings settings, T value) {
		this.customSettings.put(settings, value);
		CUSTOM_SETTINGS.computeIfAbsent(settings, s -> new HashSet<>()).add(this);
	}

	@Override
	public void build(@NotNull Item.Settings settings, Item item) {
		this.customItemSettings.put(item, this.customSettings.getOrDefault(settings, this.defaultValue.get()));
	}

	public static void onBuild(Item.Settings settings, Item item) {
		for (CustomItemSettingImpl<?> setting : CUSTOM_SETTINGS.getOrDefault(settings, Collections.emptyList())) {
			setting.build(settings, item);
		}
	}
}
