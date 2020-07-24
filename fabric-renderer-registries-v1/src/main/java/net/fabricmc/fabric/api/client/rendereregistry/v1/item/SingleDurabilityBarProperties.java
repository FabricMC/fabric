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

package net.fabricmc.fabric.api.client.rendereregistry.v1.item;

import net.minecraft.item.ItemStack;

public abstract class SingleDurabilityBarProperties implements DurabilityBarProperties {
	public abstract boolean isVisible(ItemStack stack);
	public abstract float getFillFactor(ItemStack stack);
	public abstract int getColor(ItemStack stack);

	@Override
	public int getCount(ItemStack stack) {
		return 1;
	}

	@Override
	public boolean isVisible(ItemStack stack, int index) {
		return isVisible(stack);
	}

	@Override
	public float getFillFactor(ItemStack stack, int index) {
		return getFillFactor(stack);
	}

	@Override
	public int getColor(ItemStack stack, int index) {
		return getColor(stack);
	}
}
