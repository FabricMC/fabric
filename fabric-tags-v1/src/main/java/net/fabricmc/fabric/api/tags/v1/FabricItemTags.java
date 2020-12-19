package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tag.TagRegistry;

/**
 * The place where the new item tags added by Fabric live.
 */
public final class FabricItemTags {
	/**
	 * Allows for the addition of more types of Elytra via the tag system.
	 *
	 * <p>The textures for the Elytra feature renderer are located at <code>assets/IDENTIFIER_NAMESPACE/textures/entity/elytra/IDENTIFIER_PATH.png</code>
	 */
	public static final Tag<Item> ELYTRA = TagRegistry.item(new Identifier("fabric-tags-v1", "elytra"));

	/**
	 * Allows for the addition of more types of Totems of Undying via the tag system.
	 *
	 * <p>The effect for the activation of the totem will automatically use the item that triggered it.
	 */
	public static final Tag<Item> TOTEMS_OF_UNDYING = TagRegistry.item(new Identifier("fabric-tags-v1", "totems_of_undying"));
}
