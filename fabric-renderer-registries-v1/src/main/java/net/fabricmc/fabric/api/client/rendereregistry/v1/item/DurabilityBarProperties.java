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
import net.minecraft.util.math.MathHelper;

public interface DurabilityBarProperties {
	DurabilityBarProperties DEFAULT = new SingleDurabilityBarProperties() {
		private float getDamageValue(ItemStack stack) {
			return Math.max(0, (stack.getMaxDamage() - stack.getDamage()) / (float) stack.getMaxDamage());
		}

		@Override
		public boolean isVisible(ItemStack stack) {
			return stack.isDamaged();
		}

		@Override
		public float getFillFactor(ItemStack stack) {
			return getDamageValue(stack);
		}

		@Override
		public int getColor(ItemStack stack) {
			return MathHelper.hsvToRgb(getDamageValue(stack) / 3, 1, 1);
		}
	};

	int getCount(ItemStack stack);

	boolean isVisible(ItemStack stack, int index);

	float getFillFactor(ItemStack stack, int index);

	int getColor(ItemStack stack, int index);
}
