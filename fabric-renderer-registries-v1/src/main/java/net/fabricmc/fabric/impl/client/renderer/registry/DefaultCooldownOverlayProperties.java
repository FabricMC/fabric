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

package net.fabricmc.fabric.impl.client.renderer.registry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CooldownOverlayProperties;

public class DefaultCooldownOverlayProperties implements CooldownOverlayProperties {
	protected float getCooldownAmount(ItemStack stack) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		return player == null ? 0.0F : player.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
	}

	@Override
	public boolean isVisible(ItemStack stack) {
		return getCooldownAmount(stack) > 0;
	}

	@Override
	public float getFillFactor(ItemStack stack) {
		return getCooldownAmount(stack);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x7FFFFFFF;
	}
}
