package net.fabricmc.fabric.api.resource.conditions.v1.conditions;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditionTypes;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;

public class TrueResourceCondition implements ResourceCondition {
	public static final Codec<TrueResourceCondition> CODEC = Codec.unit(TrueResourceCondition::new);

	@Override
	public ResourceConditionType<?> getType() {
		return DefaultResourceConditionTypes.TRUE;
	}

	@Override
	public boolean test() {
		return true;
	}
}
