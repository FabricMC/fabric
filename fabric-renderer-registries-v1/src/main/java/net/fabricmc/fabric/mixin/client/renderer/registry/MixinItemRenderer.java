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

package net.fabricmc.fabric.mixin.client.renderer.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRendererRegistry;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
	@Shadow
	public float zOffset;

	@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0),
			locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void on_renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci, MatrixStack matrixStack) {
		ItemOverlayRenderer ior = ItemOverlayRendererRegistry.get(stack.getItem());

		if (ior != null) {
			matrixStack.push();
			matrixStack.translate(x, y, zOffset);

			if (ior.renderOverlay(matrixStack, renderer, stack, x, y, countLabel)) {
				ci.cancel();
			}

			matrixStack.pop();
		}
	}
}
