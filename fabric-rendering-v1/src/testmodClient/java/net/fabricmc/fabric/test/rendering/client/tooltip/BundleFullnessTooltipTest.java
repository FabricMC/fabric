package net.fabricmc.fabric.test.rendering.client.tooltip;


import net.fabricmc.fabric.api.client.rendering.v1.TooltipDataCallback;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.BundleItem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class BundleFullnessTooltipTest implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TooltipDataCallback.EVENT.register((itemStack, tooltipDataList) -> {
            if (itemStack.getItem() instanceof BundleItem bundle) {
                tooltipDataList.add(0,new BundleCustomTooltipData(BundleItem.getAmountFilled(itemStack)));
            }
        });
        TooltipComponentCallback.EVENT.register(data -> {
            if(data instanceof BundleCustomTooltipData bundleCustomTooltipData)
                return new BundleFullnessTooltipComponent(bundleCustomTooltipData.fullness);
            return null;
        });
    }

    private static class BundleCustomTooltipData implements TooltipData {
        private final float fullness;

        public BundleCustomTooltipData(float fullness) {
            this.fullness = fullness;
        }
    }

    private static class BundleFullnessTooltipComponent implements TooltipComponent {
        private static final int BAR_WIDTH = 40;
        private static final int BAR_HEIGHT = 10;
        private static final int GAP = 2;
        private final float fullness;

        public BundleFullnessTooltipComponent(float fullness) {
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
            context.getMatrices().translate(x,y,0);
            context.fill(0,0,BAR_WIDTH,BAR_HEIGHT,0xFF3F007F);
            context.fill(0,0, (int) (BAR_WIDTH*fullness),BAR_HEIGHT,0xFF7F00FF);
            context.getMatrices().pop();
        }
    }
}
