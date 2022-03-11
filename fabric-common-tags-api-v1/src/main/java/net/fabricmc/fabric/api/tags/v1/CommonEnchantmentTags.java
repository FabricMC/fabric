package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.api.impl.v1.TagRegistration;

public class CommonEnchantmentTags {

	private static TagKey<Enchantment> register(String tagID) {
		return TagRegistration.ENCHANTMENT_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Enchantment> registerFabric(String tagID) {
		return TagRegistration.ENCHANTMENT_TAG_REGISTRATION.registerFabric(tagID);
	}
}
