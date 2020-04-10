package net.fabricmc.fabric.api.client.rendering.v1;

import net.minecraft.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.BuiltinItemRendererRegistryImpl;

/**
 * This registry holds {@linkplain BuiltinItemRenderer builtin item renderers} for items.
 */
@Environment(EnvType.CLIENT)
public interface BuiltinItemRendererRegistry {
	BuiltinItemRendererRegistry INSTANCE = BuiltinItemRendererRegistryImpl.INSTANCE;

	/**
	 * Registers the renderer for the item.
	 *
	 * <p>Note that the item's JSON model must also extend {@code minecraft:builtin/entity}.
	 *
	 * @param item the item
	 * @param renderer the renderer
	 */
	void register(Item item, BuiltinItemRenderer renderer);
}
