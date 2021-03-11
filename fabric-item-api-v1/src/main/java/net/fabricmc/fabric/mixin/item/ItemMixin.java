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

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Item;

import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.fabricmc.fabric.api.item.v1.CustomItemSetting;

@Mixin(Item.class)
class ItemMixin implements ItemExtensions {
	@Unique
	private final HashMap<CustomItemSetting<?>, Object> customItemSettings = new HashMap<>();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(Item.Settings settings, CallbackInfo info) {
		FabricItemInternals.onBuild(settings, this);
	}

	@Override
	public @NotNull Map<CustomItemSetting<?>, Object> fabric_getCustomItemSettings() {
		return this.customItemSettings;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> @NotNull T fabric_getCustomItemSetting(CustomItemSetting<T> type) {
		return (T) this.customItemSettings.computeIfAbsent(type, CustomItemSetting::getDefaultValue);
	}
}
