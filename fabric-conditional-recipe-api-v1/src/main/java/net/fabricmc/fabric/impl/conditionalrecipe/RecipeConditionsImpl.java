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

package net.fabricmc.fabric.impl.conditionalrecipe;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.conditionalrecipe.v1.RecipeCondition;

@ApiStatus.Internal
public final class RecipeConditionsImpl {
	private RecipeConditionsImpl() {
	}

	private static final BiMap<Identifier, RecipeCondition> CONDITIONS = HashBiMap.create();

	public static <T extends RecipeCondition> void register(Identifier id, T condition) {
		CONDITIONS.put(id, condition);
	}

	@Nullable
	public static <T extends RecipeCondition> T get(Identifier id) {
		return (T) CONDITIONS.get(id);
	}

	@Nullable
	public static <T extends RecipeCondition> Identifier getId(T condition) {
		return CONDITIONS.inverse().get(condition);
	}

	public static boolean evaluate(Identifier recipeId, JsonObject object) {
		Identifier type = new Identifier(JsonHelper.getString(object, "type"));
		RecipeCondition condition = get(type);
		if (condition == null) throw new NullPointerException("Condition '" + type + "' does not exist!");

		return condition.process(recipeId, object.get("condition"));
	}
}
