package net.fabricmc.fabric.api.resource.conditions.v1.conditions;

import com.mojang.serialization.Codec;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;

public record NotResourceCondition(ResourceCondition condition) implements ResourceCondition {
	public static final Codec<NotResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceCondition.CODEC.fieldOf("value").forGetter(NotResourceCondition::condition)
	).apply(instance, NotResourceCondition::new));

	@Override
	public ResourceConditionType<?> getType() {
		return ResourceConditionType.NOT;
	}

	@Override
	public boolean test() {
		return !this.condition().test();
	}
}
