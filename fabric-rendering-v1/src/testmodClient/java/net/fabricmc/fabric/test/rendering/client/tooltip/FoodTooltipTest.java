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
import net.minecraft.item.FoodComponent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipDataCallback;

public class FoodTooltipTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TooltipDataCallback.EVENT.register((itemStack, tooltipDataList) -> {
			if (itemStack.getItem().getFoodComponent() != null) {
				var foodData = new FoodItemData(itemStack.getItem().getFoodComponent());
				tooltipDataList.add(foodData);
			}
		});

		TooltipComponentCallback.EVENT.register(data -> {
			if (data instanceof FoodItemData foodItemData) {
				return new FoodModTooltip(foodItemData);
			}

			return null;
		});
	}

	private static class FoodItemData implements TooltipData {
		public final int hunger;
		FoodItemData(FoodComponent foodComponent) {
			this.hunger = foodComponent.getHunger();
		}
	}

	private static class FoodModTooltip implements TooltipComponent {
		private final FoodItemData food;
		private static final int SIZE = 8;
		private static final int GAP = 2;

		FoodModTooltip(FoodItemData foodItemData) {
			this.food = foodItemData;
		}

		@Override
		public int getHeight() {
			return (SIZE + GAP);
		}

		@Override
		public int getWidth(TextRenderer textRenderer) {
			return (SIZE + GAP) * food.hunger - GAP;
		}

		@Override
		public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
			context.getMatrices().push();
			context.getMatrices().translate(x, y, 0);

			for (int i = 0; i < food.hunger; i++) {
				context.fill(0, 0, SIZE, SIZE, 0xFFFFFF00);
				context.getMatrices().translate(GAP + SIZE, 0, 0);
			}

			context.getMatrices().pop();
		}
	}
}
