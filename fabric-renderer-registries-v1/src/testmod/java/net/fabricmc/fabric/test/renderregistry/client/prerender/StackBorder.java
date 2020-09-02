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

package net.fabricmc.fabric.test.renderregistry.client.prerender;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRenderer;
import net.fabricmc.fabric.test.renderregistry.common.RendererRegistriesTest;

public class StackBorder extends DrawableHelper implements ItemOverlayRenderer.Pre {
	protected static final Identifier BORDER_TEX = RendererRegistriesTest.id("textures/gui/border.png");
	protected final int color;

	public StackBorder(int color) {
		this.color = color;
	}

	public StackBorder(Formatting formatting) {
		this(getColorFromFormatting(formatting));
	}

	public static int getColorFromFormatting(Formatting formatting) {
		if (!formatting.isColor()) {
			throw new IllegalArgumentException("Formatting must be color!");
		}

		Integer colorRaw = formatting.getColorValue();
		assert colorRaw != null : "Something is *seriously* wrong with this Minecraft client...";
		return colorRaw;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean renderOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		RenderSystem.enableDepthTest();
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		x -= 1;
		y -= 1;
		Matrix4f matrix4f = matrixStack.peek().getModel();
		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = color & 255;
		MinecraftClient.getInstance().getTextureManager().bindTexture(BORDER_TEX);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(matrix4f, x, y + 18, 0).color(r, g, b, 255).texture(0, 1).next();
		bufferBuilder.vertex(matrix4f, x + 18, y + 18, 0).color(r, g, b, 255).texture(1, 1).next();
		bufferBuilder.vertex(matrix4f, x + 18, y, 0).color(r, g, b, 255).texture(1, 0).next();
		bufferBuilder.vertex(matrix4f, x, y, 0).color(r, g, b, 255).texture(0, 0).next();
		bufferBuilder.end();
		RenderSystem.enableAlphaTest();
		BufferRenderer.draw(bufferBuilder);
		return false;
	}
}
