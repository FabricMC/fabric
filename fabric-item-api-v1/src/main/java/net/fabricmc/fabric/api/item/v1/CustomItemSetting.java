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

package net.fabricmc.fabric.api.item.v1;

import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.Item;

import net.fabricmc.fabric.impl.item.CustomItemSettingImpl;

/**
 * A type of setting that can be passed to item constructors.
 * This feature can be used by mods to add non-standard settings
 * to items in a way that is compatible with other mods that add
 * settings to items.
 *
 * <p>Values of this setting can be retrieved from an item using {@link CustomItemSetting#getValue(Item)}</p>
 *
 * @param <T> the type of the setting to be attached
 */
@ApiStatus.NonExtendable
public interface CustomItemSetting<T> {
	/**
	 * Returns the current value of this setting for the given {@link Item}.
	 * Should only be called after or within item construction.
	 *
	 * @param item the item
	 * @return the current setting if present, the default setting if not
	 */
	T getValue(Item item);

	// There are a lot of these for convenience sake.
	static <T> CustomItemSetting<T> create(Supplier<T> defaultValue) {
		return new CustomItemSettingImpl<>(defaultValue);
	}

	static CustomItemSetting<Integer> create(int defaultValue) {
		return new CustomItemSettingImpl<>(() -> defaultValue);
	}

	static CustomItemSetting<Long> create(long defaultValue) {
		return new CustomItemSettingImpl<>(() -> defaultValue);
	}

	static CustomItemSetting<Float> create(float defaultValue) {
		return new CustomItemSettingImpl<>(() -> defaultValue);
	}

	static CustomItemSetting<Double> create(double defaultValue) {
		return new CustomItemSettingImpl<>(() -> defaultValue);
	}

	static CustomItemSetting<Boolean> create(boolean defaultValue) {
		return new CustomItemSettingImpl<>(() -> defaultValue);
	}

	static CustomItemSetting<String> create(String defaultValue) {
		return new CustomItemSettingImpl<>(() -> defaultValue);
	}
}
