package net.fabricmc.fabric.impl.lookup.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;

class ItemKeyCache {
	static ItemKey get(Item item, @Nullable CompoundTag tag) {
		// TODO: actually cache things
		return new ItemKeyImpl(item, tag);
	}
}
