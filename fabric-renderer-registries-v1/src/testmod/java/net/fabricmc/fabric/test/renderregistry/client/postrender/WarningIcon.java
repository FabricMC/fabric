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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.PostItemOverlayRenderer;
import net.fabricmc.fabric.test.renderregistry.common.RendererRegistriesTest;

public class WarningIcon extends DrawableHelper implements PostItemOverlayRenderer {
	private static final Identifier WARNING_TEX = RendererRegistriesTest.id("textures/gui/warning.png");

	@SuppressWarnings("deprecation")
	@Override
	public void renderOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		RenderSystem.disableDepthTest();
		RenderSystem.enableTexture();
		RenderSystem.disableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		MinecraftClient.getInstance().getTextureManager().bindTexture(WARNING_TEX);
		drawTexture(matrixStack, x - 1, y - 1, 18, 18, 18, 18, 18, 18);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
	}
}
