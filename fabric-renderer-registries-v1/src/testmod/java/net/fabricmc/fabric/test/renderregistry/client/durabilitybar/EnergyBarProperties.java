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

import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.DurabilityBarProperties;
import net.fabricmc.fabric.test.renderregistry.common.durabilitybar.StorageItem;

public class EnergyBarProperties implements DurabilityBarProperties {
	@Override
	public boolean isVisible(ItemStack stack) {
		return true;
	}

	@Override
	public float getFillFactor(ItemStack stack) {
		return ((StorageItem) stack.getItem()).getFillLevel(stack);
	}

	@Override
	public int getColor(ItemStack stack) {
		// Let's go with green for an energy bar
		return 0xFF00;
	}
}
