package net.fabricmc.fabric.impl.client.rendering.tooltip;

import net.minecraft.client.item.TooltipData;

import java.util.ArrayList;
import java.util.Optional;

/**
 * This class stores multiple TooltipData object to their further mapping to MultiTooltipComponent
 */
public class MultiTooltipData extends ArrayList<TooltipData> implements TooltipData {
    public MultiTooltipData(int length) {
        super(length);
    }
}
