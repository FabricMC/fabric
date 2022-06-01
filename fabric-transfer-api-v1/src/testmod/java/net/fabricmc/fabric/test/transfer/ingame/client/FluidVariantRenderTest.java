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

package net.fabricmc.fabric.test.transfer.ingame.client;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

/**
 * Renders the water sprite in the top left of the screen, to make sure that it correctly depends on the position.
 */
public class FluidVariantRenderTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null) return;

			int renderY = 0;
			List<FluidVariant> variants = Arrays.asList(FluidVariant.of(Fluids.WATER), FluidVariant.of(Fluids.LAVA));

			for (FluidVariant variant : variants) {
				Sprite[] sprites = FluidVariantRendering.getSprites(variant);
				int color = FluidVariantRendering.getColor(variant, player.world, player.getBlockPos());

				if (sprites != null) {
					drawFluidInGui(matrices, sprites[0], color, 0, renderY);
					renderY += 16;
					drawFluidInGui(matrices, sprites[1], color, 0, renderY);
					renderY += 16;
				}

				List<Text> tooltip = FluidVariantRendering.getTooltip(variant);
				TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

				renderY += 2;

				for (Text line : tooltip) {
					textRenderer.draw(matrices, line, 4, renderY, -1);
					renderY += 10;
				}
			}
		});
	}

	private static void drawFluidInGui(MatrixStack ms, Sprite sprite, int color, int i, int j) {
		if (sprite == null) return;

		MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

		float r = ((color >> 16) & 255) / 255f;
		float g = ((color >> 8) & 255) / 255f;
		float b = (color & 255) / 255f;
		RenderSystem.disableDepthTest();

		RenderSystem.enableTexture();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(4, VertexFormats.POSITION_COLOR_TEXTURE);
		float x0 = (float) i;
		float y0 = (float) j;
		float x1 = x0 + 16;
		float y1 = y0 + 16;
		float z = 0.5f;
		float u0 = sprite.getMinU();
		float v0 = sprite.getMinV();
		float u1 = sprite.getMaxU();
		float v1 = sprite.getMaxV();
		Matrix4f model = ms.peek().getModel();
		bufferBuilder.vertex(model, x0, y1, z).color(r, g, b, 1).texture(u0, v1).next();
		bufferBuilder.vertex(model, x1, y1, z).color(r, g, b, 1).texture(u1, v1).next();
		bufferBuilder.vertex(model, x1, y0, z).color(r, g, b, 1).texture(u1, v0).next();
		bufferBuilder.vertex(model, x0, y0, z).color(r, g, b, 1).texture(u0, v0).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);

		RenderSystem.enableDepthTest();
	}
}
