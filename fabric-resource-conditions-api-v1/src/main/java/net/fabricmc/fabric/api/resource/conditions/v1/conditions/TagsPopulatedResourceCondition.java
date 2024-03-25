package net.fabricmc.fabric.api.resource.conditions.v1.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public record TagsPopulatedResourceCondition(Identifier registry, List<Identifier> tags) implements ResourceCondition {
	public static final Codec<TagsPopulatedResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Identifier.CODEC.fieldOf("registry").orElse(RegistryKeys.ITEM.getValue()).forGetter(TagsPopulatedResourceCondition::registry),
		Identifier.CODEC.listOf().fieldOf("values").forGetter(TagsPopulatedResourceCondition::tags)
	).apply(instance, TagsPopulatedResourceCondition::new));

	@SafeVarargs
	public <T> TagsPopulatedResourceCondition(Identifier registry, TagKey<T>... tags) {
		this(registry, Arrays.stream(tags).map(TagKey::id).toList());
	}
	@Override
	public ResourceConditionType<?> getType() {
		return ResourceConditionType.ANY_MODS_LOADED;
	}

	@Override
	public boolean test() {
		return ResourceConditionsImpl.tagsPopulated(this.registry(), this.tags());
	}
}
