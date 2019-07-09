package net.fabricmc.fabric.api.renderer.v1;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.DebugHudCallback;

public class RenderingAPIClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		DebugHudCallback.EVENT_LEFT.register(list -> {
			if (RendererAccess.INSTANCE.hasRenderer()) {
				list.add("[Fabric] Active renderer: " + RendererAccess.INSTANCE.getRenderer().getClass().getSimpleName());
			} else {
				list.add("[Fabric] Active renderer: none (vanilla)");
			}
		});
	}
}
