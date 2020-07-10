package net.fabricmc.fabric.impl.client.renderer.registry;

import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRenderer;

public interface ItemHooks {
	ItemOverlayRenderer getItemOverlayRenderer();

	void setItemOverlayRenderer(ItemOverlayRenderer ior);
}
