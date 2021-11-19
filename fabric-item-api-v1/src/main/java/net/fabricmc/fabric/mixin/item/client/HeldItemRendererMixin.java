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

package net.fabricmc.fabric.mixin.item.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.item.v1.FabricItem;

/**
 * Allow canceling the held item update animation if {@link FabricItem#allowNbtUpdateAnimation} returns false.
 */
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
	@Shadow
	private ItemStack mainHand;

	@Shadow
	private ItemStack offHand;

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "updateHeldItems", at = @At("HEAD"))
	private void modifyProgressAnimation(CallbackInfo ci) {
		// Modify main hand
		ItemStack newMainStack = client.player.getMainHandStack();

		if (mainHand.getItem() == newMainStack.getItem()) {
			if (!((FabricItem) mainHand.getItem()).allowNbtUpdateAnimation(client.player, Hand.MAIN_HAND, mainHand, newMainStack)) {
				mainHand = newMainStack;
			}
		}

		// Modify off hand
		ItemStack newOffStack = client.player.getOffHandStack();

		if (offHand.getItem() == newOffStack.getItem()) {
			if (!((FabricItem) offHand.getItem()).allowNbtUpdateAnimation(client.player, Hand.OFF_HAND, offHand, newOffStack)) {
				offHand = newOffStack;
			}
		}
	}
}
