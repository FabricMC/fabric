package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.tag.TagKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.api.impl.v1.TagRegistration;

public class CommonBiomeTags {

	private static TagKey<Biome> register(String tagID) {
		return TagRegistration.BIOME_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Biome> registerFabric(String tagID) {
		return TagRegistration.BIOME_TAG_REGISTRATION.registerFabric(tagID);
	}
}
