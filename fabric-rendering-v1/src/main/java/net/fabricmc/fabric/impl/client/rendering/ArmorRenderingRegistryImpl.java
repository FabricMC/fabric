package net.fabricmc.fabric.impl.client.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class ArmorRenderingRegistryImpl implements ArmorRenderingRegistry {
	public static final ArmorRenderingRegistryImpl INSTANCE = new ArmorRenderingRegistryImpl();

	private static final HashMap<Item, ArmorRenderer> RENDERERS = new HashMap<>();

	@Override
	public void register(ArmorRenderer renderer, Item... items) {
		for (Item item : items) {
			Objects.requireNonNull(item, "armor item is null");
			Objects.requireNonNull(renderer, "renderer is null");
			if (RENDERERS.putIfAbsent(item, renderer) != null) {
				throw new IllegalArgumentException("Custom armor renderer already exists for " + Registry.ITEM.getId(item));
			}
		}
	}

	@Nullable
	public static ArmorRenderer getRenderer(Item item){
		return RENDERERS.get(item);
	}
}
