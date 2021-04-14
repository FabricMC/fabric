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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.AnimationUpdateHandler;

@Environment(EnvType.CLIENT)
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

		if (newMainStack.getItem().equals(mainHand.getItem())) {
			AnimationUpdateHandler updateAnimationHandler = AnimationUpdateHandler.get(newMainStack.getItem());

			// If an update animation handler for the main hand exists, and it denies the update,
			//    assign the new stack to the cached stack to prevent the update.
			if (updateAnimationHandler != null) {
				if (!updateAnimationHandler.shouldAnimateUpdate(mainHand, newMainStack)) {
					mainHand = newMainStack;
				}
			}
		}

		// Modify off-hand
		ItemStack newOffStack = client.player.getMainHandStack();

		if (newOffStack.getItem().equals(offHand.getItem())) {
			AnimationUpdateHandler updateAnimationHandler = AnimationUpdateHandler.get(newOffStack.getItem());

			// If an update animation handler for the off hand exists, and it denies the update,
			//    assign the new stack to the cached stack to prevent the update.
			if (updateAnimationHandler != null) {
				if (!updateAnimationHandler.shouldAnimateUpdate(offHand, newOffStack)) {
					offHand = newOffStack;
				}
			}
		}
	}
}
