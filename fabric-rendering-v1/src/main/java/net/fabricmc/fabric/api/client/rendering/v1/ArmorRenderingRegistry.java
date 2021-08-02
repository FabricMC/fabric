package net.fabricmc.fabric.api.client.rendering.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.ArmorRenderingRegistryImpl;
import net.minecraft.item.Item;

/**
 * This registry holds {@linkplain ArmorRenderer custom armor renderers} for armor items.
 */
@Environment(EnvType.CLIENT)
public interface ArmorRenderingRegistry {
	/**
	 * The singleton instance of the renderer registry.
	 * Use this instance to call the methods in this interface.
	 */
	ArmorRenderingRegistryImpl INSTANCE = ArmorRenderingRegistryImpl.INSTANCE;

	/**
	 * Registers the armor renderer for the specified items
	 * @param renderer	the renderer
	 * @param items		the items
	 * @throws IllegalArgumentException if an item already has a registered armor renderer
	 * @throws NullPointerException if either an item or the renderer is null
	 */
	void register(ArmorRenderer renderer, Item... items);
}
