package net.fabricmc.fabric.test.rendering.client.tooltip;


import net.fabricmc.fabric.api.client.rendering.v1.TooltipDataCallback;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;


public class DurabilityTooltipTest implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TooltipDataCallback.EVENT.register((itemStack, tooltipDataList) -> {
            if (itemStack.isDamageable()) {
                tooltipDataList.add(new DamagedItemData(itemStack.getDamage(), itemStack.getMaxDamage()));
            }
        });
        TooltipComponentCallback.EVENT.register(data -> {
            if(data instanceof DamagedItemData damagedItemData)
                return new DurabilityModTooltipComponent(damagedItemData);
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

        public DurabilityModTooltipComponent(DamagedItemData data) {
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
            context.getMatrices().translate(x,y,0);
            float width = 1-(float) this.damage.durability / this.damage.maxDurability;
            context.fill(0,0,BAR_WIDTH,BAR_HEIGHT,0xFFFF0000);
            context.fill(0,0, (int) (BAR_WIDTH*width),BAR_HEIGHT,0xFF00FF00);
            context.getMatrices().pop();
        }
    }
}
