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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.test.item.CustomSettingExtension;
import net.fabricmc.fabric.test.item.FabricItemSettingsTests;

@Mixin(Item.class)
public class ItemMixin implements CustomSettingExtension {
	@Unique
	private String testStringFromCustomSetting;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(Item.Settings settings, CallbackInfo ci) {
		if (settings instanceof FabricItemSettings) {
			this.testStringFromCustomSetting = FabricItemSettingsTests.CUSTOM_DATA_TEST.getValue(settings);
		}
	}

	@Override
	public String getTestString() {
		return this.testStringFromCustomSetting;
	}
}
