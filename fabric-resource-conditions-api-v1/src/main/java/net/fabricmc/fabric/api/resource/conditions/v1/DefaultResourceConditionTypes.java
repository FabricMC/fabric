package net.fabricmc.fabric.api.resource.conditions.v1;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.resource.conditions.v1.conditions.AllModsLoadedResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.conditions.AndResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.conditions.AnyModsLoadedResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.conditions.FeaturesEnabledResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.conditions.NotResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.conditions.OrResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.conditions.RegistryContainsResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.conditions.TagsPopulatedResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.conditions.TrueResourceCondition;

import net.minecraft.util.Identifier;

public class DefaultResourceConditionTypes {
	public static final ResourceConditionType<TrueResourceCondition> TRUE = createResourceConditionType("true", TrueResourceCondition.CODEC);
	public static final ResourceConditionType<NotResourceCondition> NOT = createResourceConditionType("not", NotResourceCondition.CODEC);
	public static final ResourceConditionType<OrResourceCondition> OR = createResourceConditionType("or", OrResourceCondition.CODEC);
	public static final ResourceConditionType<AndResourceCondition> AND = createResourceConditionType("and", AndResourceCondition.CODEC);
	public static final ResourceConditionType<AllModsLoadedResourceCondition> ALL_MODS_LOADED = createResourceConditionType("all_mods_loaded", AllModsLoadedResourceCondition.CODEC);
	public static final ResourceConditionType<AnyModsLoadedResourceCondition> ANY_MODS_LOADED = createResourceConditionType("any_mods_loaded", AnyModsLoadedResourceCondition.CODEC);
	public static final ResourceConditionType<TagsPopulatedResourceCondition> TAGS_POPULATED = createResourceConditionType("tags_populated", TagsPopulatedResourceCondition.CODEC);
	public static final ResourceConditionType<FeaturesEnabledResourceCondition> FEATURES_ENABLED = createResourceConditionType("features_enabled", FeaturesEnabledResourceCondition.CODEC);
	public static final ResourceConditionType<RegistryContainsResourceCondition> REGISTRY_CONTAINS = createResourceConditionType("registry_contains", RegistryContainsResourceCondition.CODEC);

	private static <T extends ResourceCondition> ResourceConditionType<T> createResourceConditionType(String name, Codec<T> codec) {
		return ResourceConditionType.create(new Identifier("fabric", name), codec);
	}

	public static void init() {

	}

	static {
		ResourceConditions.register(TRUE);
		ResourceConditions.register(NOT);
		ResourceConditions.register(AND);
		ResourceConditions.register(OR);
		ResourceConditions.register(ALL_MODS_LOADED);
		ResourceConditions.register(ANY_MODS_LOADED);
		ResourceConditions.register(TAGS_POPULATED);
		ResourceConditions.register(FEATURES_ENABLED);
		ResourceConditions.register(REGISTRY_CONTAINS);
	}
}
