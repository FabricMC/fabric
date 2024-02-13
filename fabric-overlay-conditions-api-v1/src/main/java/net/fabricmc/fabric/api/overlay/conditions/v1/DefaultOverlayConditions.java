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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * Contains overlay conditions provided by Fabric itself.
 */
public final class DefaultOverlayConditions {
	private static final Identifier NOT = new Identifier("fabric:not");
	private static final Identifier ALL_OF = new Identifier("fabric:all_of");
	private static final Identifier ANY_OF = new Identifier("fabric:any_of");
	private static final Identifier MOD_LOADED = new Identifier("fabric:mod_loaded");

	static void init() {
		// init static
	}

	static {
		OverlayConditions.register(NOT, object -> {
			JsonObject condition = JsonHelper.getObject(object, "condition");
			return !OverlayConditions.conditionMatches(condition);
		});
		OverlayConditions.register(ALL_OF, object -> {
			JsonArray array = JsonHelper.getArray(object, "conditions");
			return OverlayConditions.conditionsMatch(array, true);
		});
		OverlayConditions.register(ANY_OF, object -> {
			JsonArray array = JsonHelper.getArray(object, "conditions");
			return OverlayConditions.conditionsMatch(array, false);
		});
		OverlayConditions.register(MOD_LOADED, object -> {
			JsonElement modId = JsonHelper.getElement(object, "mod_id");
			return OverlayConditions.modLoaded(modId);
		});
	}

	private DefaultOverlayConditions() {
	}
}
