package net.fabricmc.fabric.impl.resource.conditions.conditions;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.impl.resource.conditions.DefaultResourceConditionTypes;
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
