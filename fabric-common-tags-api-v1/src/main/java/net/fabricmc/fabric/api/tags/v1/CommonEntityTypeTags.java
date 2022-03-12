package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.common.tag.TagRegistration;

public class CommonEntityTypeTags {
	/**
	 * Tag containing entity types that display a boss health bar.
	 */
	public static final TagKey<EntityType<?>> BOSSES = register("bosses");
	public static final TagKey<EntityType<?>> MINECARTS = register("minecarts");
	public static final TagKey<EntityType<?>> BOATS = register("boats");

	private static TagKey<EntityType<?>> register(String tagID) {
		return TagRegistration.ENTITY_TYPE_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<EntityType<?>> registerFabric(String tagID) {
		return TagRegistration.ENTITY_TYPE_TAG_REGISTRATION.registerFabric(tagID);
	}
}
