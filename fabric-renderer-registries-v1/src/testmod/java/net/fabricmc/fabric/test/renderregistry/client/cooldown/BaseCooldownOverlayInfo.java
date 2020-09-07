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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemCooldownOverlayInfo;

public abstract class BaseCooldownOverlayInfo implements ItemCooldownOverlayInfo {
	protected float getCooldownAmount(ItemStack stack, MinecraftClient client) {
		// copied from ItemRenderer.renderGuiItemOverlay, lines 355-356 (player was local "clientPlayerEntity", client was MinecraftClient.getInstance())
		ClientPlayerEntity player = client.player;
		return player == null ? 0.0F : player.getItemCooldownManager().getCooldownProgress(stack.getItem(), client.getTickDelta());
	}

	@Override
	public boolean isVisible(ItemStack stack, MinecraftClient client) {
		// copied from ItemRenderer.renderGuiItemOverlay, line 357 (getCooldownAmount call was local "k")
		return getCooldownAmount(stack, client) > 0;
	}

	@Override
	public float getFillFactor(ItemStack stack, MinecraftClient client) {
		return getCooldownAmount(stack, client);
	}

	@Override
	public int getColor(ItemStack stack, MinecraftClient client) {
		return 0x7FFFFFFF;
	}
}
