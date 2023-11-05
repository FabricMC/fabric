package net.fabricmc.fabric.api.client.rendering.v1;

import java.util.List;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface TooltipDataCallback {
    /**
     * Allows registering custom TooltipData object for item.
     * This allows you to add your own tooltips to existing items
     * Tooltip data rendering should be registered using TooltipComponentCallback,
     * otherwise game will crash when trying to map TooltipData to TooltipComponent
     * If you don't need to add tooltip data to this specific itemStack you can return Optional.empty()
     */
    public static final Event<TooltipDataCallback> EVENT = EventFactory.createArrayBacked(TooltipDataCallback.class, callbacks -> (itemStack, tooltipDataList) -> {
        //MultiTooltipData tooltipData = new MultiTooltipData(callbacks.length);
        for(TooltipDataCallback callback : callbacks){
            callback.getTooltipData(itemStack,tooltipDataList);
        }
    });
    void getTooltipData(ItemStack itemStack, List<TooltipData> tooltipDataList);
}


