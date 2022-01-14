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

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

/**
 * A resource condition and its parameters that can be serialized to JSON, meant for use in data generators.
 */
public interface ConditionJsonProvider {
	/**
	 * Write the passed conditions to a JSON object in the {@value ResourceConditions#CONDITIONS_KEY} array.
	 *
	 * @throws IllegalArgumentException if the JSON object already contains that array
	 */
	static void write(JsonObject conditionalObject, ConditionJsonProvider @Nullable... conditions) {
		if (conditions == null) { // no condition -> skip
			return;
		}

		Preconditions.checkArgument(conditions.length > 0, "Must write at least one condition."); // probably a programmer error
		if (conditionalObject.has(ResourceConditions.CONDITIONS_KEY)) throw new IllegalArgumentException("Object already has a condition entry: " + conditionalObject);

		JsonArray array = new JsonArray();

		for (ConditionJsonProvider condition : conditions) {
			array.add(condition.toJson());
		}

		conditionalObject.add(ResourceConditions.CONDITIONS_KEY, array);
	}

	/**
	 * Serialize this condition and its parameters to a new JSON object.
	 */
	default JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(ResourceConditions.CONDITION_ID_KEY, getConditionId().toString());
		this.writeParameters(jsonObject);
		return jsonObject;
	}

	/**
	 * {@return the identifier of this condition} This is only for use by {@link #toJson()} to write it.
	 */
	Identifier getConditionId();

	/**
	 * Write the condition parameters (everything except the {@code "condition": ...} entry). This is only for use by {@link #toJson()}.
	 */
	void writeParameters(JsonObject object);
}
