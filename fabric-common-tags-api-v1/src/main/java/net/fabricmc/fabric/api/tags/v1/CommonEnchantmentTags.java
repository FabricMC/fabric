package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.v1.TagRegistration;

public class CommonEnchantmentTags {
	/**
	 * A tag containing enchantments that increase the amount or quality of drops from blocks.
	 */
	public static final TagKey<Enchantment> FORTUNE = register("fortune");
	/**
	 * A tag containing enchantments that increase the amount or quality of drops from entities.
	 */
	public static final TagKey<Enchantment> LOOTING = register("looting");
	/**
	 * A tag containing enchantments that cause a block to drop itself in item form
	 * rather than some other item, such as {@link net.minecraft.enchantment.Enchantments#SILK_TOUCH}.
	 */
	public static final TagKey<Enchantment> IDENTICAL_DROPS = register("identical_drops");

	private static TagKey<Enchantment> register(String tagID) {
		return TagRegistration.ENCHANTMENT_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Enchantment> registerFabric(String tagID) {
		return TagRegistration.ENCHANTMENT_TAG_REGISTRATION.registerFabric(tagID);
	}
}
