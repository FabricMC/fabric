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

package net.fabricmc.fabric.api.overlay.conditions.v1;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Registration and access to overlay conditions.
 * An overlay condition is an identified {@code Predicate<JsonObject>} that can decide whether a resource pack overlay is applied.
 * <ul>
 *     <li>The lower-level {@link #conditionsMatch} and {@link #conditionMatches} may be useful when implementing conditions.</li>
 *     <li>Conditions are registered with {@link #register} and queried with {@link #get}.</li>
 * </ul>
 *
 * <h3>Details of the format</h3>
 *
 * <p>A mod's pack.mcmeta file must have a {@link #OVERLAYS_KEY} entry, containing an {@link #ENTRIES_KEY} containing an array of condition objects.
 * The conditions in the array must be satisfied to apply the overlay.
 * Each condition object must contain a {@link #CONDITION_TYPE_KEY} entry with the identifier of the condition,
 * and it may also contain additional data for the condition.
 * Here is an example of a pack.mcmeta file that overlays the {@code example} directory if no mod with id {@code a} is loaded:
 * <pre>{@code
 * {
 *   ... // Normal contents of the pack.mcmeta file
 *   "fabric:overlays": {
 *     "entries": [ // An array of condition objects
 *       { // A condition object
 *         // The directory to overlay if the condition is met.
 *         "directory": "example",
 *         // The identifier of the condition. The "fabric:not" condition inverts the condition in its "condition" field
 *         "type": "fabric:not",
 *         // Additional data. For "fabric:not", the "condition" field is required to be another condition object.
 *         "condition": {
 *           // The identifier of the condition.
 *           "condition": "fabric:mod_loaded",
 *           // Additional data. For "fabric:mod_loaded", the "mod_id" field is the mod id.
 *           "mod_id": "a"
 *         }
 *       }
 *     ]
 *   }
 * }
 * }</pre>
 */
public final class OverlayConditions {
	private static final Map<Identifier, Predicate<JsonObject>> REGISTERED_CONDITIONS = new ConcurrentHashMap<>();
	/**
	 * The key ({@value}) Fabric uses to identify if custom overlays are in a pack.mcmeta file.
	 */
	public static final String OVERLAYS_KEY = "fabric:overlays";

	/**
	 * The key ({@value}) Fabric uses to identify custom overlays in a custom overlay object.
	 */
	public static final String ENTRIES_KEY = "entries";

	/**
	 * The key ({@value}) identifying the overlay condition's identifier inside a condition object.
	 */
	public static final String CONDITION_TYPE_KEY = "type";

	/**
	 * Register a new overlay condition.
	 *
	 * @throws IllegalArgumentException If an overlay condition is already registered with the same name.
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
	 * If {@code matchAll} is true, check if all the passed conditions match.
	 * If it is false, check if at least one of the passed conditions matches.
	 *
	 * @throws JsonParseException If some condition failed to parse.
	 */
	public static boolean conditionsMatch(JsonArray conditions, boolean matchAll) {
		for (JsonElement element : conditions) {
			if (element.isJsonObject()) {
				if (conditionMatches(element.getAsJsonObject()) != matchAll) {
					return !matchAll;
				}
			} else {
				throw new JsonParseException("Invalid condition entry: " + element);
			}
		}

		return matchAll;
	}

	/**
	 * Check if the passed condition object matches.
	 *
	 * @throws JsonParseException If some condition failed to parse.
	 */
	public static boolean conditionMatches(JsonObject condition) {
		Identifier conditionId = Identifier.tryParse(JsonHelper.getString(condition, CONDITION_TYPE_KEY));
		Objects.requireNonNull(conditionId, "Invalid identifier for condition type");
		Predicate<JsonObject> jrc = get(conditionId);

		if (jrc == null) {
			throw new JsonParseException("Unknown overlay condition: " + conditionId);
		} else {
			return jrc.test(condition);
		}
	}

	/**
	 * Check if a mod with the passed mod id string exists.
	 *
	 * @throws JsonParseException If the mod id failed to parse.
	 */
	public static boolean modLoaded(JsonElement modId) {
		if (modId instanceof JsonPrimitive) {
			return FabricLoader.getInstance().isModLoaded(modId.getAsString());
		} else {
			throw new JsonParseException("Invalid mod_id element: " + modId);
		}
	}

	private OverlayConditions() {
	}

	static {
		// Load Fabric-provided conditions.
		DefaultOverlayConditions.init();
	}
}
