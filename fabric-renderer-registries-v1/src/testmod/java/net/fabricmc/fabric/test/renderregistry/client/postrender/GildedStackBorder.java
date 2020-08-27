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

package net.fabricmc.fabric.test.renderregistry.client.postrender;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.PostItemOverlayRenderer;

public class GildedStackBorder extends DrawableHelper implements PostItemOverlayRenderer {
	@Override
	public void renderOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		Integer colorRaw = Formatting.GOLD.getColorValue();
		assert colorRaw != null : "Something is *seriously* wrong with this Minecraft client instance...";
		int color = colorRaw;
		RenderSystem.disableTexture();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
		/*
		drawHorizontalLine(matrixStack, x, x + 16, y, color);
		drawHorizontalLine(matrixStack, x, x + 16, y + 16, color);
		drawVerticalLine(matrixStack, x, y + 1, y + 15, color);
		drawVerticalLine(matrixStack, x + 16, y + 1, y + 15, color);
		 */
		final int r = color & 0xFF;
		final int g = (color << 8) & 0xFF;
		final int b = (color << 16) & 0xFF;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuffer();
		buf.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		buf.vertex(0, 0, 0).color(r, g, b, 0xFF).next();
		buf.vertex(16, 0, 0).color(r, g, b, 0xFF).next();
		buf.vertex(16, 16, 0).color(r, g, b, 0xFF).next();
		buf.vertex(0, 16, 0).color(r, g, b, 0xFF).next();
		tess.draw();
		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}
}
