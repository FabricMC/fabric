package net.fabricmc.fabric.api.resource.conditions.v1.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public record FeaturesEnabledResourceCondition(List<Identifier> features) implements ResourceCondition {
	public static final Codec<FeaturesEnabledResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Identifier.CODEC.listOf().fieldOf("features").forGetter(FeaturesEnabledResourceCondition::features)
	).apply(instance, FeaturesEnabledResourceCondition::new));

	public FeaturesEnabledResourceCondition(Identifier... features) {
		this(List.of(features));
	}
	@Override
	public ResourceConditionType<?> getType() {
		return ResourceConditionType.FEATURES_ENABLED;
	}

	@Override
	public boolean test() {
		return ResourceConditionsImpl.featuresEnabled(this.features());
	}
}
