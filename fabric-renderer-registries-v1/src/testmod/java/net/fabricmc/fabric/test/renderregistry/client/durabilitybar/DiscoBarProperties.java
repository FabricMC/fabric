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

package net.fabricmc.fabric.test.renderregistry.client.durabilitybar;

import java.awt.Color;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.DurabilityBarProperties;

public class DiscoBarProperties implements DurabilityBarProperties {
	@Override
	public int getCount(ItemStack stack) {
		return 1;
	}

	@Override
	public boolean isVisible(ItemStack stack, int index) {
		return true;
	}

	@Override
	public float getFillFactor(ItemStack stack, int index) {
		return 0;
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		// This doesn't need to be pretty, but it shows that
		// one can get fancy with durability bars by taking
		// the current time into account when calculating fill factor
		// or color.
		float c = (Util.getMeasuringTimeMs() % 500) / 500f;
		return Color.HSBtoRGB(c, 1.0f, 1.0f);
	}
}
