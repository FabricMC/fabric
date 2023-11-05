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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipDataCallback;

public class DurabilityTooltipTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TooltipDataCallback.EVENT.register((itemStack, tooltipDataList) -> {
			if (itemStack.isDamageable()) {
				tooltipDataList.add(new DamagedItemData(itemStack.getDamage(), itemStack.getMaxDamage()));
			}
		});

		TooltipComponentCallback.EVENT.register(data -> {
			if (data instanceof DamagedItemData damagedItemData) {
				return new DurabilityModTooltipComponent(damagedItemData);
			}

			return null;
		});
	}

	public record DamagedItemData(int durability, int maxDurability) implements TooltipData {
	}

	private static class DurabilityModTooltipComponent implements TooltipComponent {
		private static final int BAR_WIDTH = 40;
		private static final int BAR_HEIGHT = 10;
		private static final int GAP = 2;
		private final DamagedItemData damage;

		DurabilityModTooltipComponent(DamagedItemData data) {
			this.damage = data;
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
			float width = 1-(float) this.damage.durability / this.damage.maxDurability;
			context.fill(0, 0, BAR_WIDTH, BAR_HEIGHT, 0xFFFF0000);
			context.fill(0, 0, (int) (BAR_WIDTH * width), BAR_HEIGHT, 0xFF00FF00);
			context.getMatrices().pop();
		}
	}
}
