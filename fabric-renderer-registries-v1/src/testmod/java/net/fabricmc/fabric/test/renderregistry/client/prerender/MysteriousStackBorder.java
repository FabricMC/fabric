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

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.PreItemOverlayRenderer;

public class MysteriousStackBorder extends DrawableHelper implements PreItemOverlayRenderer {
	@Override
	public boolean renderOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		// TODO this... doesn't render anything. it *is* called, Tessellator just kinda refuses to do anything
		Integer colorRaw = Formatting.DARK_PURPLE.getColorValue();
		assert colorRaw != null : "Something is *seriously* wrong with this Minecraft client instance...";
		int color = colorRaw;
		setZOffset(200);
		drawHorizontalLine(matrixStack, x, x + 16, y, color);
		drawHorizontalLine(matrixStack, x, x + 16, y + 16, color);
		drawVerticalLine(matrixStack, x, y + 1, y + 15, color);
		drawVerticalLine(matrixStack, x + 16, y + 1, y + 15, color);
		return true;
	}
}
