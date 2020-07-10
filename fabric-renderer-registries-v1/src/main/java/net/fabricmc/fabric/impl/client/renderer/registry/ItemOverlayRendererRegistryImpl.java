package net.fabricmc.fabric.impl.client.renderer.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRendererRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

public class ItemOverlayRendererRegistryImpl implements ItemOverlayRendererRegistry {
	private static final Object2ObjectLinkedOpenHashMap<Item, ItemOverlayRenderer> OVERLAY_RENDERERS =
	    new Object2ObjectLinkedOpenHashMap<>();

	@Override
	public ItemOverlayRenderer get(ItemConvertible item) {
		return OVERLAY_RENDERERS.get(item.asItem());
	}

	@Override
	public void add(ItemConvertible item, ItemOverlayRenderer overlayRenderer) {
		OVERLAY_RENDERERS.put(item.asItem(), overlayRenderer);
	}

	@Override
	public void remove(ItemConvertible item, ItemOverlayRenderer overlayRenderer) {
		OVERLAY_RENDERERS.remove(item.asItem());
	}
}
