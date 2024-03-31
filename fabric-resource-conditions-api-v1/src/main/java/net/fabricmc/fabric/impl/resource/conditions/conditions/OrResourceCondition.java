package net.fabricmc.fabric.impl.resource.conditions.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.impl.resource.conditions.DefaultResourceConditionTypes;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

import net.minecraft.util.dynamic.Codecs;

import java.util.List;

public record OrResourceCondition(List<ResourceCondition> conditions) implements ResourceCondition {
	public static final Codec<OrResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codecs.nonEmptyList(ResourceCondition.CODEC.listOf()).fieldOf("values").forGetter(OrResourceCondition::conditions)
	).apply(instance, OrResourceCondition::new));

	@Override
	public ResourceConditionType<?> getType() {
		return DefaultResourceConditionTypes.OR;
	}

	@Override
	public boolean test() {
		return ResourceConditionsImpl.conditionsMet(this.conditions(), false);
	}
}
