package net.fabricmc.fabric.impl.client.rendering;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;

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
		if (RENDERERS.containsKey(item)) {
			throw new IllegalArgumentException("Item " + item + " already has a builtin renderer!");
		}

		RENDERERS.put(item, renderer);
	}

	public static BuiltinItemRenderer getRenderer(Item item) {
		return RENDERERS.get(item);
	}
}
