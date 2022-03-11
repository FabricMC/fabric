package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.v1.TagRegistration;

public class CommonEntityTypeTags {

	private static TagKey<EntityType<?>> register(String tagID) {
		return TagRegistration.ENTITY_TYPE_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<EntityType<?>> registerFabric(String tagID) {
		return TagRegistration.ENTITY_TYPE_TAG_REGISTRATION.registerFabric(tagID);
	}
}
