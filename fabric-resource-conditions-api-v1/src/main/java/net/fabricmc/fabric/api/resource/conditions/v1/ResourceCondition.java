/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.resource.conditions.v1;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

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
