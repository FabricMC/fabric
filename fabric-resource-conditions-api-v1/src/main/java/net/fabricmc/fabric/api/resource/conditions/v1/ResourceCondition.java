package net.fabricmc.fabric.api.resource.conditions.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.util.Arrays;
import java.util.List;

public interface ResourceCondition {
	Codec<ResourceCondition> CODEC = ResourceConditionType.TYPE_CODEC.dispatch("condition", ResourceCondition::getType, ResourceConditionType::codec);
	Codec<List<ResourceCondition>> CONDITIONS_CODEC = CODEC.listOf().fieldOf(ResourceConditions.CONDITIONS_KEY).codec();
	static void addConditions(JsonObject baseObject, ResourceCondition... conditions) {
		if (baseObject.has(ResourceConditions.CONDITIONS_KEY)) {
			throw new IllegalArgumentException("Object already has a condition entry: " + baseObject);
		}

        Either<JsonElement, DataResult.PartialResult<JsonElement>> conditionsResult = CODEC.listOf().encodeStart(JsonOps.INSTANCE, Arrays.asList(conditions)).get();
		if (conditionsResult.left().isPresent()) {
			baseObject.add(ResourceConditions.CONDITIONS_KEY, conditionsResult.left().get());
		} else {
			throw new IllegalArgumentException("Could not parse resource conditions");
		}
	}
	ResourceConditionType<?> getType();
	boolean test();

}
