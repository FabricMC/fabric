package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.v1.TagRegistration;

public class CommonEnchantmentTags {
	/**
	 * A tag containing enchantments that increase or otherwise improve drops from blocks.
	 */
	public static final TagKey<Enchantment> FORTUNE = register("fortune");
	/**
	 * A tag containing enchantments that increase or otherwise improve drops from entities.
	 */
	public static final TagKey<Enchantment> LOOTING = register("looting");

	private static TagKey<Enchantment> register(String tagID) {
		return TagRegistration.ENCHANTMENT_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Enchantment> registerFabric(String tagID) {
		return TagRegistration.ENCHANTMENT_TAG_REGISTRATION.registerFabric(tagID);
	}
}
