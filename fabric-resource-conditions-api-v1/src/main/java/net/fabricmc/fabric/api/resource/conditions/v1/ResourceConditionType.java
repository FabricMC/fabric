package net.fabricmc.fabric.api.resource.conditions.v1;

import com.mojang.serialization.Codec;

import com.mojang.serialization.DataResult;

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

import java.util.Optional;

public interface ResourceConditionType<T extends ResourceCondition> {
	Codec<ResourceConditionType<?>> TYPE_CODEC = Identifier.CODEC.comapFlatMap(id ->
		Optional.ofNullable(ResourceConditions.getConditionType(id)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown resource condition key: "+ id )),
		ResourceConditionType::id
	);
	ResourceConditionType<TrueResourceCondition> TRUE = ResourceConditionType.create("true", TrueResourceCondition.CODEC);
	ResourceConditionType<NotResourceCondition> NOT = ResourceConditionType.create("not", NotResourceCondition.CODEC);
	ResourceConditionType<OrResourceCondition> OR = ResourceConditionType.create("or", OrResourceCondition.CODEC);
	ResourceConditionType<AndResourceCondition> AND = ResourceConditionType.create("and", AndResourceCondition.CODEC);
	ResourceConditionType<AllModsLoadedResourceCondition> ALL_MODS_LOADED = ResourceConditionType.create("all_mods_loaded", AllModsLoadedResourceCondition.CODEC);
	ResourceConditionType<AnyModsLoadedResourceCondition> ANY_MODS_LOADED = ResourceConditionType.create("any_mods_loaded", AnyModsLoadedResourceCondition.CODEC);
	ResourceConditionType<TagsPopulatedResourceCondition> TAGS_POPULATED = ResourceConditionType.create("tags_populated", TagsPopulatedResourceCondition.CODEC);
	ResourceConditionType<FeaturesEnabledResourceCondition> FEATURES_ENABLED = ResourceConditionType.create("features_enabled", FeaturesEnabledResourceCondition.CODEC);
	ResourceConditionType<RegistryContainsResourceCondition> REGISTRY_CONTAINS = ResourceConditionType.create("registry_contains", RegistryContainsResourceCondition.CODEC);

	Identifier id();
	Codec<T> codec();

	private static <T extends ResourceCondition> ResourceConditionType<T> create(String name, Codec<T> codec) {
		return create(new Identifier("fabric", name), codec);
	}
	static <T extends ResourceCondition> ResourceConditionType<T> create(Identifier id, Codec<T> codec) {
		return new ResourceConditionType<>() {
			@Override
			public Identifier id() {
				return id;
			}

			@Override
			public Codec<T> codec() {
				return codec;
			}
		};
	}

    static void init() {

    }
}
