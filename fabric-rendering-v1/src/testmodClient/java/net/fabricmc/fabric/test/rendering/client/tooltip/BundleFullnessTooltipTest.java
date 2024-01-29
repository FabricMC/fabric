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

package net.fabricmc.fabric.test.rendering.client.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.BundleItem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipDataCallback;

public class BundleFullnessTooltipTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TooltipDataCallback.EVENT.register((itemStack, tooltipDataList) -> {
			if (itemStack.getItem() instanceof BundleItem bundle) {
				tooltipDataList.add(0, new BundleCustomTooltipData(BundleItem.getAmountFilled(itemStack)));
			}
		});
		TooltipComponentCallback.EVENT.register(data -> {
			if (data instanceof BundleCustomTooltipData bundleCustomTooltipData) {
				return new BundleFullnessTooltipComponent(bundleCustomTooltipData.fullness);
			}

			return null;
		});
	}

	private static class BundleCustomTooltipData implements TooltipData {
		private final float fullness;
		BundleCustomTooltipData(float fullness) {
			this.fullness = fullness;
		}
	}

	private static class BundleFullnessTooltipComponent implements TooltipComponent {
		private static final int BAR_WIDTH = 40;
		private static final int BAR_HEIGHT = 10;
		private static final int GAP = 2;
		private final float fullness;

		BundleFullnessTooltipComponent(float fullness) {
			this.fullness = fullness;
		}

		@Override
		public int getHeight() {
			return BAR_HEIGHT + GAP;
		}

		@Override
		public int getWidth(TextRenderer textRenderer) {
			return BAR_WIDTH;
		}

		@Override
		public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
			context.getMatrices().push();
			context.getMatrices().translate(x, y, 0);
			context.fill(0, 0, BAR_WIDTH, BAR_HEIGHT, 0xFF3F007F);
			context.fill(0, 0, (int) (BAR_WIDTH * fullness), BAR_HEIGHT, 0xFF7F00FF);
			context.getMatrices().pop();
		}
	}
}
