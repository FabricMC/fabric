package net.fabricmc.fabric.impl.resource.conditions.conditions;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.impl.resource.conditions.DefaultResourceConditionTypes;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

import net.minecraft.util.dynamic.Codecs;

public record AndResourceCondition(List<ResourceCondition> conditions) implements ResourceCondition {
	public static final Codec<AndResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codecs.nonEmptyList(ResourceCondition.CODEC.listOf()).fieldOf("values").forGetter(AndResourceCondition::conditions)
	).apply(instance, AndResourceCondition::new));

	@Override
	public ResourceConditionType<?> getType() {
		return DefaultResourceConditionTypes.AND;
	}

	@Override
	public boolean test() {
		return ResourceConditionsImpl.conditionsMet(this.conditions(), true);
	}
}
