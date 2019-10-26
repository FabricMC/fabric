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

package net.fabricmc.indigo.renderer.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.indigo.renderer.render.ItemRenderContext;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
	@Shadow
	protected abstract void renderQuads(BufferBuilder bufferBuilder, List<BakedQuad> quads, int color, ItemStack stack);
	@Shadow
	protected ItemColors colorMap;
	private final ThreadLocal<ItemRenderContext> CONTEXTS = ThreadLocal.withInitial(() -> new ItemRenderContext(colorMap));

	/**
	 * Save stack for enchantment glint renders - we won't otherwise have access to it
	 * during the glint render because it receives an empty stack.
	 */
	@Inject(at = @At("HEAD"), method = "renderItemAndGlow")
	private void hookRenderItemAndGlow(ItemStack stack, BakedModel model, CallbackInfo ci) {
		if (stack.hasEnchantmentGlint() && !((FabricBakedModel)model).isVanillaAdapter()) {
			CONTEXTS.get().enchantmentStack = stack;
		}
	}

	@Inject(at = @At("HEAD"), method = "renderModel", cancellable = true)
	private void hookRenderModel(BakedModel model, int color, ItemStack stack, CallbackInfo ci) {
		FabricBakedModel fabricModel = (FabricBakedModel)model;

		if (!fabricModel.isVanillaAdapter()) {
			CONTEXTS.get().renderModel(fabricModel, color, stack, this::renderQuads);
			ci.cancel();
		}
	}
}
