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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.JsonDataLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

/**
 * Registration and access to resource loading conditions.
 * A resource condition is an identified {@code Predicate<JsonObject>} that can decide whether a resource should be loaded or not.
 * <ul>
 *     <li>A JSON object that may contain a condition can be parsed with {@link #objectMatchesConditions}.
 *     This is the preferred way of implementing conditional objects, as it handles the details of the format (see below) and catches and logs thrown exceptions.
 *     This function should only be called from the "apply" phase of a {@link net.minecraft.resource.ResourceReloader},
 *     otherwise some conditions might behave in unexpected ways.
 *     </li>
 *     <li>The lower-level {@link #conditionsMatch} and {@link #conditionMatches} may be useful when implementing conditions.</li>
 *     <li>Conditions are registered with {@link #register} and queried with {@link #get}.</li>
 * </ul>
 *
 * <p>At the moment, Fabric only recognizes conditions for resources loaded by subclasses of {@link JsonDataLoader}.
 * This means: recipes, advancements, loot tables, loot functions and loot conditions.
 *
 * <p>Fabric provides some conditions, which can be generated using the helper methods in {@link DefaultResourceConditions}.
 *
 * <h3>Details of the format</h3>
 *
 * <p>A conditional JSON object must have a {@link #CONDITIONS_KEY} entry, containing an array of condition objects.
 * The conditions in the array must be satisfied to load the resource.
 * Each condition object must contain a {@link #CONDITION_ID_KEY} entry with the identifier of the condition,
 * and it may also contain additional data for the condition.
 * Here is an example of a resource that is only loaded if no mod with id {@code a} is loaded:
 * <pre>{@code
 * {
 *   ... // normal contents of the resource
 *   "fabric:load_conditions": [ // array of condition objects
 *     { // a condition object
 *       // the identifier of the condition... the "fabric:not" condition inverts the condition in its "value" field
 *       "condition": "fabric:not",
 *       // additional data, for "fabric:not", the "value" field is required to be another condition object
 *       "value": {
 *         // the identifier of the condition
 *         "condition": "fabric:all_mods_loaded",
 *         // additional data, for "fabric:all_mods_loaded"
 *         "values": [
 *           "a"
 *         ]
 *       }
 *     }
 *   ]
 * }
 * }</pre>
 */
public final class ResourceConditions {
	private static final Map<Identifier, Predicate<JsonObject>> REGISTERED_CONDITIONS = new ConcurrentHashMap<>();
	/**
	 * The key ({@value}) Fabric uses to identify resource conditions in a JSON object.
	 */
	public static final String CONDITIONS_KEY = "fabric:load_conditions";
	/**
	 * The key ({@value}) identifying the resource condition's identifier inside a condition object.
	 */
	public static final String CONDITION_ID_KEY = "condition";

	/**
	 * Register a new resource condition.
	 *
	 * @throws IllegalArgumentException If a resource condition is already registered with the same name.
	 */
	public static void register(Identifier identifier, Predicate<JsonObject> condition) {
		Objects.requireNonNull(identifier, "Identifier may not be null.");
		Objects.requireNonNull(condition, "Condition may not be null.");

		if (REGISTERED_CONDITIONS.put(identifier, condition) != null) {
			throw new IllegalArgumentException("Duplicate JSON condition registration with id " + identifier);
		}
	}

	/**
	 * Get the resource condition with the passed name, or {@code null} if none is registered (yet).
	 */
	@Nullable
	public static Predicate<JsonObject> get(Identifier identifier) {
		return REGISTERED_CONDITIONS.get(identifier);
	}

	/**
	 * Check if the passed JSON object either has no {@code fabric:conditions} tag, or all of its conditions match.
	 * This should be called for objects that may contain a conditions entry.
	 *
	 * <p>This function should only be called from the "apply" phase of a {@link net.minecraft.resource.ResourceReloader},
	 * otherwise some conditions might behave in unexpected ways.
	 *
	 * <p>If an exception is thrown during condition testing, it will be caught and logged, and false will be returned.
	 */
	public static boolean objectMatchesConditions(JsonObject object) {
		try {
			JsonArray conditions = JsonHelper.getArray(object, CONDITIONS_KEY, null);

			if (conditions == null) {
				return true; // no conditions
			} else {
				return conditionsMatch(conditions, true);
			}
		} catch (RuntimeException exception) {
			ResourceConditionsImpl.LOGGER.warn("Skipping object %s. Failed to parse resource conditions".formatted(object), exception);
			return false;
		}
	}

	/**
	 * If {@code and} is true, check if all the passed conditions match.
	 * If it is false, check if at least one of the passed conditions matches.
	 *
	 * @throws RuntimeException If some condition failed to parse.
	 */
	public static boolean conditionsMatch(JsonArray conditions, boolean and) throws RuntimeException {
		for (JsonElement element : conditions) {
			if (element.isJsonObject()) {
				if (conditionMatches(element.getAsJsonObject()) != and) {
					return !and;
				}
			} else {
				throw new JsonParseException("Invalid condition entry: " + element);
			}
		}

		return and;
	}

	/**
	 * Check if the passed condition object matches.
	 *
	 * @throws RuntimeException If some condition failed to parse.
	 */
	public static boolean conditionMatches(JsonObject condition) throws RuntimeException {
		Identifier conditionId = new Identifier(JsonHelper.getString(condition, CONDITION_ID_KEY));
		Predicate<JsonObject> jrc = get(conditionId);

		if (jrc == null) {
			throw new JsonParseException("Unknown recipe condition: " + conditionId);
		} else {
			return jrc.test(condition);
		}
	}

	private ResourceConditions() {
	}

	static {
		// Load Fabric-provided conditions.
		DefaultResourceConditions.init();
	}
}
