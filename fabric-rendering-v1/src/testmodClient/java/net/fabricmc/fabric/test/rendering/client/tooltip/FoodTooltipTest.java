package net.fabricmc.fabric.test.rendering.client.tooltip;


import net.fabricmc.fabric.api.client.rendering.v1.TooltipDataCallback;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.FoodComponent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

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
            if(data instanceof FoodItemData foodItemData)
                return new FoodModTooltip(foodItemData);
            return null;
        });
    }


    private static class FoodItemData implements TooltipData {
        public final int hunger;

        public FoodItemData(FoodComponent foodComponent) {
            this.hunger = foodComponent.getHunger();
        }
    }

    private static class FoodModTooltip implements TooltipComponent {
        private final FoodItemData food;
        private static final int SIZE=8;
        private static final int GAP=2;

        public FoodModTooltip(FoodItemData foodItemData) {
            this.food = foodItemData;
        }

        @Override
        public int getHeight() {
            return (SIZE+GAP);
        }

        @Override
        public int getWidth(TextRenderer textRenderer) {
            return (SIZE+GAP)*food.hunger-GAP;
        }
        @Override
        public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
            context.getMatrices().push();
            context.getMatrices().translate(x,y,0);
            for(int i=0;i<food.hunger;i++){
                context.fill(0,0,SIZE,SIZE,0xFFFFFF00);
                context.getMatrices().translate(GAP+SIZE,0,0);
            }
            context.getMatrices().pop();
        }
    }
}
