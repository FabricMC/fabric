package net.fabricmc.fabric.api.client.rendereregistry.v1;

import net.fabricmc.fabric.impl.client.renderer.registry.ItemOverlayRendererRegistryImpl;
import net.minecraft.item.ItemConvertible;

public interface ItemOverlayRendererRegistry {
	ItemOverlayRendererRegistry INSTANCE = new ItemOverlayRendererRegistryImpl();

	ItemOverlayRenderer get(ItemConvertible item);
	void add(ItemConvertible item, ItemOverlayRenderer overlayRenderer);
	void remove(ItemConvertible item, ItemOverlayRenderer overlayRenderer);
}
