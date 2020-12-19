package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tag.TagRegistry;

/**
 * The place where the new block tags added by Fabric live.
 */
public final class FabricBlockTags {
	/**
	 * Allows for the addition of more types of Bookshelves via the tag system.
	 *
	 * <p>This allows the enchantment table particles and enchantment levels to work with custom blocks.
	 */
	public static final Tag<Block> BOOKSHELVES = TagRegistry.block(new Identifier("fabric-tags-v1", "bookshelves"));
}
