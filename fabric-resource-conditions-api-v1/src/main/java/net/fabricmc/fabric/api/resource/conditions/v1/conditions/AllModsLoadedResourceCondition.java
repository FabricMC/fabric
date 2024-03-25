package net.fabricmc.fabric.api.resource.conditions.v1.conditions;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditionTypes;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

import net.minecraft.util.dynamic.Codecs;

public record AllModsLoadedResourceCondition(List<String> modIds) implements ResourceCondition {
	public static final Codec<AllModsLoadedResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codecs.nonEmptyList(Codec.STRING.listOf()).fieldOf("values").forGetter(AllModsLoadedResourceCondition::modIds)
	).apply(instance, AllModsLoadedResourceCondition::new));

	@Override
	public ResourceConditionType<?> getType() {
		return DefaultResourceConditionTypes.ALL_MODS_LOADED;
	}

	@Override
	public boolean test() {
		return ResourceConditionsImpl.modsLoaded(this.modIds(), true);
	}
}
