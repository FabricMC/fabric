package net.fabricmc.fabric.impl.client.rendering;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

@Environment(EnvType.CLIENT)
public enum BuiltinItemRendererRegistryImpl implements BuiltinItemRendererRegistry {
	INSTANCE;

	private static final Map<Item, BuiltinItemRenderer> RENDERERS = new HashMap<>();

	@Override
	public void register(Item item, BuiltinItemRenderer renderer) {
		if (RENDERERS.put(item, renderer) != null) {
			throw new IllegalArgumentException("Item " + Registry.ITEM.getId(item) + " already has a builtin renderer!");
		}
	}

	/* @Nullable */
	public static BuiltinItemRenderer getRenderer(Item item) {
		return RENDERERS.get(item);
	}
}
