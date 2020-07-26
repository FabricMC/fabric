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

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CooldownOverlayProperties;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CountLabelProperties;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.DurabilityBarProperties;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRendererRegistry;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
	@Shadow public float zOffset;
	@Shadow
	protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

	@Unique
	protected void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int color) {
		renderGuiQuad(buffer, x, y, width, height, color >> 16 & 255, color >> 8 & 255, color & 255, color >> 24 & 255);
	}

	@Unique private final MatrixStack matrixStack = new MatrixStack();

	/**
	 * @reason Implement custom item overlay API
	 * @author ADudeCalledLeo
	 */
	@SuppressWarnings("deprecation")
	@Overwrite
	public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		if (!stack.isEmpty()) {
			matrixStack.push();
			matrixStack.translate(0.0D, 0.0D, this.zOffset + 200.0F);

			if (ItemOverlayRendererRegistry.getPreRenderer(stack.getItem()).renderOverlay(matrixStack, renderer, stack, x, y, countLabel)) {
				return;
			}

			CountLabelProperties countProps = ItemOverlayRendererRegistry.getCountLabelProperties(stack.getItem());

			if (countProps.isVisible(stack, countLabel)) {
				String string = countProps.getContents(stack, countLabel);
				int color = countProps.getColor(stack, countLabel);
				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
				renderer.draw(string, x + 17 - renderer.getWidth(string), y + 9, color, true, matrixStack.peek().getModel(), immediate, false, 0, 0xF000F0);
				immediate.draw();
			}

			DurabilityBarProperties barProps = ItemOverlayRendererRegistry.getDurabilityBarProperties(stack.getItem());

			final int barCount = barProps.getCount(stack);

			if (barCount > 0) {
				int barY = 13 - (barCount - 1) * 2;
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.disableAlphaTest();
				RenderSystem.disableBlend();

				for (int i = 0; i < barCount; i++) {
					if (barProps.isVisible(stack, i)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						int width = Math.round(barProps.getFillFactor(stack, i) * 13.0F);
						int color = barProps.getColor(stack, i);
						this.renderGuiQuad(bufferBuilder, x + 2, y + barY, 13, 2, 0xFF000000);
						this.renderGuiQuad(bufferBuilder, x + 2, y + barY, width, 1, color);
						barY += 2;
					}
				}

				RenderSystem.enableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}

			CooldownOverlayProperties coolProps = ItemOverlayRendererRegistry.getCooldownOverlayProperties(stack.getItem());

			if (coolProps.isVisible(stack)) {
				float fillFactor = coolProps.getFillFactor(stack);
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
				this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - fillFactor)), 16, MathHelper.ceil(16.0F * fillFactor), coolProps.getColor(stack));
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}

			ItemOverlayRendererRegistry.getPostRenderer(stack.getItem()).renderOverlay(matrixStack, renderer, stack, x, y, countLabel);

			matrixStack.pop();
		}
	}
}
