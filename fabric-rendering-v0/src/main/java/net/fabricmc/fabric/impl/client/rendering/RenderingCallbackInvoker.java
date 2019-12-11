package net.fabricmc.fabric.impl.client.rendering;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;

public class RenderingCallbackInvoker implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		InvalidateRenderStateCallback.EVENT.register(() -> net.fabricmc.fabric.api.client.render.InvalidateRenderStateCallback.EVENT.invoker().onInvalidate());
	}
}
