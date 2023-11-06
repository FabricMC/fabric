/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a matrixCopy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.client.rendering.tooltip;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.VertexConsumerProvider;

/**
 * This class renders multiple tooltip components as one.
 */
public class MultiTooltipComponent implements TooltipComponent {
	private final int height;
	private final int width;
	private final List<TooltipComponent> components;

	public static MultiTooltipComponent of(List<TooltipData> data) {
		var l = new ArrayList<TooltipComponent>(data.size());

		for (TooltipData d : data) {
			l.add(TooltipComponent.of(d));
		}

		return new MultiTooltipComponent(l);
	}

	public MultiTooltipComponent(List<TooltipComponent> components) {
		this.components = components;
		int height = 0;
		int width = 0;

		for (TooltipComponent component : components) {
			height += component.getHeight();
			width = Math.max(width, component.getWidth(MinecraftClient.getInstance().textRenderer));
		}

		this.height = height;
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return width;
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
		Matrix4f matrixCopy = new Matrix4f(matrix);

		for (TooltipComponent c : components) {
			c.drawText(textRenderer, x, y, matrixCopy, vertexConsumers);
			matrixCopy.translate(0, c.getHeight(), 0);
		}
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
		context.getMatrices().push();

		for (TooltipComponent c : components) {
			c.drawItems(textRenderer, x, y, context);
			context.getMatrices().translate(0, c.getHeight(), 0);
		}

		context.getMatrices().pop();
	}
}
