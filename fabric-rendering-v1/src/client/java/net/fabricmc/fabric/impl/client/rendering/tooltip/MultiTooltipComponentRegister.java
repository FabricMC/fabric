package net.fabricmc.fabric.impl.client.rendering.tooltip;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class MultiTooltipComponentRegister implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TooltipComponentCallback.EVENT.register((tooltipData)->{
			if(tooltipData instanceof MultiTooltipData multiTooltipData){
				return MultiTooltipComponent.of(multiTooltipData);
			}
			return null;
		});
	}
}
