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
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CustomItemOverlayRenderer;
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

	/**
	 * @reason Implement custom item overlay API
	 * @author ADudeCalledLeo
	 */
	@SuppressWarnings("deprecation")
	@Overwrite
	public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		if (!stack.isEmpty()) {
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.translate(0.0D, 0.0D, this.zOffset + 200.0F);

			CustomItemOverlayRenderer cior = ItemOverlayRendererRegistry.getCustom(stack.getItem());

			if (cior != null) {
				if (cior.renderOverlay(matrixStack, renderer, stack, x, y, countLabel)) {
					return;
				}
			}

			CountLabelProperties clp = ItemOverlayRendererRegistry.getCountLabelProperties(stack.getItem());

			if (clp.isVisible(stack, countLabel)) {
				int color = clp.getColor(stack, countLabel);
				countLabel = clp.getContents(stack, countLabel);
				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
				renderer.draw(countLabel, x + 17 - renderer.getWidth(countLabel), y + 9, color, true, matrixStack.peek().getModel(), immediate, false, 0, 0xF000F0);
				immediate.draw();
			}

			DurabilityBarProperties dbp = ItemOverlayRendererRegistry.getDurabilityBarProperties(stack.getItem());

			final int dbCount = dbp.getCount(stack);

			if (dbCount > 0) {
				int dbY = 13 - (dbCount - 1) * 2;
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.disableAlphaTest();
				RenderSystem.disableBlend();

				for (int dbI = 0; dbI < dbCount; dbI++) {
					if (dbp.isVisible(stack, dbI)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						int i = Math.round(13.0F - dbp.getFillFactor(stack, dbI) * 13.0F);
						int j = dbp.getColor(stack, dbI);
						this.renderGuiQuad(bufferBuilder, x + 2, y + dbY, 13, 2, 0xFF000000);
						this.renderGuiQuad(bufferBuilder, x + 2, y + dbY, i, 1, j);
						dbY += 2;
					}
				}

				RenderSystem.enableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}

			CooldownOverlayProperties cop = ItemOverlayRendererRegistry.getCooldownOverlayProperties(stack.getItem());

			if (cop.isVisible(stack)) {
				float k = cop.getFillFactor(stack);
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
				this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - k)), 16, MathHelper.ceil(16.0F * k), cop.getColor(stack));
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}
		}
	}
}
