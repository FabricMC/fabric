package net.fabricmc.fabric.impl.item;

import net.fabricmc.fabric.api.item.ItemPropertyRegistry;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.tag.Tag;

public class CompostingChanceRegistry implements ItemPropertyRegistry<Float> {
	public static final CompostingChanceRegistry INSTANCE = new CompostingChanceRegistry();

	@Override
	public void add(ItemProvider item, Float value) {
		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item, value);
	}

	@Override
	public void add(Tag<Item> tag, Float value) {
		throw new RuntimeException("Tags currently not supported!");
	}

	@Override
	public void remove(ItemProvider item) {
		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.remove(item);
	}

	@Override
	public void remove(Tag<Item> tag) {
		throw new RuntimeException("Tags currently not supported!");
	}
}
