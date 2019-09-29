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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.indigo.renderer.render.ItemRenderContext;
import net.fabricmc.indigo.renderer.render.ItemRenderContext.VanillaQuadHandler;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
//	@Shadow
//	protected abstract void method_23182(BakedModel model, ItemStack stack, int color, class_4587 matrixStack, class_4588 buffer);
//
//	@Shadow
//	protected ItemColors colorMap;
//
//	private final VanillaQuadHandler vanillaHandler = this::method_23182;
//
//	private final ThreadLocal<ItemRenderContext> CONTEXTS = ThreadLocal.withInitial(() -> new ItemRenderContext(colorMap));
//
//	@Inject(at = @At("HEAD"), method = "method_23180", cancellable = true)
//	private void hook_method_23182(BakedModel model, ItemStack stack, int color, class_4587 matrixStack, class_4588 buffer, CallbackInfo ci) {
//		final FabricBakedModel fabricModel = (FabricBakedModel) model;
//
//		if (!fabricModel.isVanillaAdapter()) {
//			CONTEXTS.get().renderModel(fabricModel, stack, color, matrixStack, buffer, vanillaHandler);
//			ci.cancel();
//		}
//	}
}
