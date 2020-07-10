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

package net.fabricmc.fabric.api.client.rendereregistry.v1;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

public interface ItemOverlayRenderer {
	/**
	 * @return <code>true</code> to cancel vanilla overlay rendering.
	 */
	boolean renderOverlay(MatrixStack matrixStack, float zOffset, TextRenderer renderer, ItemStack stack, int x, int y,
						  String countLabel);

	default void renderCountLabel(MatrixStack matrixStack, float zOffset, TextRenderer renderer, ItemStack stack,
								  int x, int y, String countLabel) {
		if (stack.getCount() != 1 || countLabel != null) {
			String string = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
			matrixStack.push();
			matrixStack.translate(0.0D, 0.0D, zOffset + 200.0F);
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			renderer.draw(string, (float)(x + 19 - 2 - renderer.getWidth(string)), (float)(y + 6 + 3), 0xFFFFFF,
			    true, matrixStack.peek().getModel(), immediate, false, 0, 0xF000F0);
			immediate.draw();
			matrixStack.pop();
		}
	}

	@SuppressWarnings("deprecation")
	default void renderDurabilityBar(MatrixStack matrixStack, float zOffset, ItemStack stack, int x, int y) {
		if (stack.isDamaged()) {
			matrixStack.push();
			matrixStack.translate(0, 0, zOffset);
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.disableAlphaTest();
			RenderSystem.disableBlend();
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuffer();
			float damage = (float)stack.getDamage();
			float damageMax = (float)stack.getMaxDamage();
			float damageVal = Math.max(0.0F, (damageMax - damage) / damageMax);
			int i = Math.round(13.0F - damage * 13.0F / damageMax);
			int j = MathHelper.hsvToRgb(damageVal / 3.0F, 1.0F, 1.0F);
			renderQuad(matrixStack, buf, x + 2, y + 13, 13, 2, 0, 0, 0, 0xFF);
			renderQuad(matrixStack, buf, x + 2, y + 13, i, 1,
					   (j >> 16) & 0xFF, (j >> 8) & 0xFF, j & 0xFF, 0xFF);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
			matrixStack.pop();
		}
	}

	default void renderCooldownOverlay(MatrixStack matrixStack, float zOffset, ItemStack stack, int x, int y) {
		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		float cooldown = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
		if (cooldown > 0.0F) {
			matrixStack.push();
			matrixStack.translate(0, 0, zOffset);
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuffer();
			renderQuad(matrixStack, buf, x, y + MathHelper.floor(16.0F * (1.0F - cooldown)), 16, MathHelper.ceil(16.0F * cooldown),
					   0xFF, 0xFF, 0xFF, 0x7F);
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
			matrixStack.pop();
		}
	}

	default void renderQuad(MatrixStack matrixStack, BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		Matrix4f mat = matrixStack.peek().getModel();
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		buffer.vertex(mat, x, y, 0).color(red, green, blue, alpha).next();
		buffer.vertex(mat, x, y + height, 0).color(red, green, blue, alpha).next();
		buffer.vertex(mat, x + width, y + height, 0).color(red, green, blue, alpha).next();
		buffer.vertex(mat, x + width, y, 0).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}
}
