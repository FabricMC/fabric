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

package net.fabricmc.fabric.impl.item.client;

import java.util.List;

import org.joml.Matrix4f;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

record BundledTooltipComponentImpl(List<TooltipComponent> list) implements TooltipComponent {
	@Override
	public int getHeight() {
		int h = 0;

		for (TooltipComponent component : list) {
			h += component.getHeight();
		}

		return h;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		int w = 0;

		for (TooltipComponent component : list) {
			w = Math.max(w, component.getWidth(textRenderer));
		}

		return w;
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
		int y1 = y;

		for (TooltipComponent component : list) {
			component.drawText(textRenderer, x, y1, matrix, vertexConsumers);
			y1 += component.getHeight();
		}
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		int y1 = y;

		for (TooltipComponent component : list) {
			component.drawItems(textRenderer, x, y1, matrices, itemRenderer, z);
			y1 += component.getHeight();
		}
	}
}
