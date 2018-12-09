package net.fabricmc.fabric.tags;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

/**
 * Helper methods for registering Tags.
 */
public final class TagRegistry {
	private TagRegistry() {

	}

	public static Tag<Block> block(Identifier id) {
		return new TagDelegate<Block>(id, null) {
			private TagContainer<Block> container;

			@Override
			protected void onAccess() {
				if (container != BlockTags.getContainer()) {
					container = BlockTags.getContainer();
					delegate = container.getOrCreate(this.getId());
				}
			}
		};
	}

	public static Tag<Item> item(Identifier id) {
		return new ItemTags.class_3490(id);
	}
}
