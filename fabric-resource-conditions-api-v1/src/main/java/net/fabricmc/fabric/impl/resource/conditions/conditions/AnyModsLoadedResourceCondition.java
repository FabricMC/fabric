package net.fabricmc.fabric.impl.resource.conditions.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.impl.resource.conditions.DefaultResourceConditionTypes;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

import net.minecraft.util.dynamic.Codecs;

import java.util.List;

public record AnyModsLoadedResourceCondition(List<String> modIds) implements ResourceCondition {
	public static final Codec<AnyModsLoadedResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codecs.nonEmptyList(Codec.STRING.listOf()).fieldOf("values").forGetter(AnyModsLoadedResourceCondition::modIds)
	).apply(instance, AnyModsLoadedResourceCondition::new));

	@Override
	public ResourceConditionType<?> getType() {
		return DefaultResourceConditionTypes.ANY_MODS_LOADED;
	}

	@Override
	public boolean test() {
		return ResourceConditionsImpl.modsLoaded(this.modIds(), false);
	}
}
