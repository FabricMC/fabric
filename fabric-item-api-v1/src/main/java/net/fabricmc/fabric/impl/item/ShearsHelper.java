package net.fabricmc.fabric.impl.item;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.fabric.impl.tag.convention.TagRegistration;

public final class ShearsHelper {
	public final static TagKey<Item> FABRIC_SHEARS = TagRegistration.ITEM_TAG_REGISTRATION.registerFabric("shears");
	public static final List<RegistryEntry<Item>> SHEARS;

	public static boolean isShears(ItemStack stack) {
		return stack.isIn(FABRIC_SHEARS) || stack.getItem() instanceof ShearsItem;
	}

	private ShearsHelper() {
	}

	static {
		ImmutableList.Builder<RegistryEntry<Item>> builder = new ImmutableList.Builder<>();
		for (Item item : Registries.ITEM) {
			@SuppressWarnings("deprecation")
			RegistryEntry<Item> entry = item.getRegistryEntry();
			if (entry.isIn(FABRIC_SHEARS) || item instanceof ShearsItem)
				builder.add(entry);
		}
		SHEARS = builder.build();
	}
}
