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

package net.fabricmc.fabric.test.renderregistry.client.cooldown;

import java.awt.Color;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.fabric.impl.client.renderer.registry.item.DefaultCooldownOverlayProperties;

/**
 * Shows a full red, flashing overlay if there's more than 80% of the cooldown still remaining.
 */
public class FlashingCooldownProperties extends DefaultCooldownOverlayProperties {
	@Override
	public float getFillFactor(ItemStack stack, MinecraftClient client) {
		if (getCooldownAmount(stack, client) > 0.8f) {
			return 1.0f;
		}

		return super.getFillFactor(stack, client);
	}

	@Override
	public int getColor(ItemStack stack, MinecraftClient client) {
		// Between 1 and 0.5 color it red, otherwise use the default color
		if (getCooldownAmount(stack, client) > 0.8f) {
			float a = 0.1f + 0.7f * (float) Math.sin((Util.getMeasuringTimeMs() % 250) / 250.0f * (float) Math.PI);
			a = MathHelper.clamp(a, 0f, 1f);

			return new Color(1.0f, 0.0f, 0.0f, a).getRGB();
		}

		return super.getColor(stack, client);
	}
}
