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

package net.fabricmc.fabric.test.screenlayer;

import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.screenlayer.v1.ScreenLayer;

public class ExampleScreen extends Screen {
	private final String screenNumberLabel;
	private final int screenNumber;
	private ButtonWidget addScreenButton;
	private ButtonWidget closeScreenButton;
	private ButtonWidget closeAllScreensButton;

	public ExampleScreen(int count) {
		super(Text.literal("Example Screen: " + count));
		this.screenNumber = count;
		this.screenNumberLabel = Integer.toString(screenNumber);
	}

	@Override
	public void init() {
		addScreenButton = ButtonWidget.builder(Text.literal("Add Screen"), (button) ->
				ScreenLayer.push(new ExampleScreen(ScreenLayer.getScreenLayerCount() + 1))).build();

		closeScreenButton = ButtonWidget.builder(Text.literal("Close Screen"), (button) -> ScreenLayer.pop()).build();
		closeAllScreensButton = ButtonWidget.builder(Text.literal("Clear All Layers"), (button) -> ScreenLayer.clear()).build();
		this.addDrawableChild(positionWidget(addScreenButton, 0, this.height - 40));
		this.addDrawableChild(positionWidget(closeScreenButton, (this.width / 2) - 50, this.height - 40));
		this.addDrawableChild(positionWidget(closeAllScreensButton, this.width - 100, this.height - 40));
	}

	private ButtonWidget positionWidget(ButtonWidget button, int x, int y) {
		button.setX(x);
		button.setY(y);
		button.setWidth(100);
		return button;
	}

	@Override
	public void render(DrawContext drawContext, int x, int y, float partialTicks) {
		KeyBinding.unpressAll();
		super.renderBackground(drawContext, x, y, partialTicks);
		int xLoc = screenNumber == 0 ? 40 : 40 * screenNumber;

		// calling size display here prevents the label and box from being affected by minecraft's gui scale video setting
		sizeDisplay(client.getWindow().getWidth(), client.getWindow().getHeight());
		drawLabel(drawContext, screenNumberLabel, xLoc, 0, 5);
		sizeDisplay(width, height);

		if (this.screenNumber == ScreenLayer.getScreenLayerCount() || ScreenLayer.getScreenLayerCount() == 0) {
			super.render(drawContext, x, y, partialTicks);
		}
	}

	public static void drawLabel(DrawContext drawContext, final String text, double x, double y, double fontScale) {
		final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

		final double width = textRenderer.getWidth(text);
		int height = textRenderer.fontHeight + (textRenderer.isRightToLeft() ? 0 : 6);
		drawContext.getMatrices().push();

		try {
			if (fontScale != 1) {
				x = x / fontScale;
				y = y / fontScale;
				drawContext.getMatrices().scale((float) fontScale, (float) fontScale, 1);
			}

			float textX = (float) x;
			float textY = (float) y;
			double rectX = x;
			double rectY = y;

			textX = (float) (x - (width / 2) + (fontScale > 1 ? .5 : 0));
			rectX = (float) (x - (Math.max(1, width) / 2) + (fontScale > 1 ? .5 : 0));

			rectY = y;
			textY = (float) (rectY + (height - textRenderer.fontHeight) / 2.0);
			// Draw background
			drawRectangle(drawContext, (float) (rectX - 2 - .5), (float) rectY, (float) ((float) (width + (2 * 2))), height);
			// Font renderer really doesn't like mid-pixel text rendering
			drawContext.getMatrices().translate(textX - Math.floor(textX), textY - Math.floor(textY), 0);
			drawContext.drawText(textRenderer, text, (int) textX, (int) textY, 15728880, true);
		} finally {
			drawContext.getMatrices().pop();
		}
	}

	public static void drawRectangle(DrawContext drawContext, float x, float y, float width, double height) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix4f, x, (float) height + y, (float) 0).color(0x89000000).next();
		bufferBuilder.vertex(matrix4f, x + width, (float) (height + y), (float) 0).color(0x89000000).next();
		bufferBuilder.vertex(matrix4f, x + width, y, 0).color(0x89000000).next();
		bufferBuilder.vertex(matrix4f, x, y, 0).color(0x89000000).next();

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.disableBlend();
	}

	public void sizeDisplay(double width, double height) {
		if (this.width > 0 && this.height > 0) {
			RenderSystem.clear(GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
			Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) width, (float) height, 0.0F, 100.0F, ScreenLayer.getFarPlane());
			RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_Z);
			MatrixStack stack = RenderSystem.getModelViewStack();
			stack.loadIdentity();
			stack.translate(0.0D, 0.0D, 1000.0F - ScreenLayer.getFarPlane());
		}
	}

	@Override
	public void renderInGameBackground(DrawContext context) {
		context.fillGradient(0, 0, this.width, this.height, 0x3D353838, 0x3D353838);
	}

	@Override
	public boolean shouldPause() {
		return true;
	}
}
