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

package net.fabricmc.fabric.api.conditionalrecipe.v1;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.conditionalrecipe.RecipeConditionsImpl;

public final class RecipeConditions {
	private RecipeConditions() {
	}

	public static <T extends RecipeCondition> void register(Identifier id, T condition) {
		RecipeConditionsImpl.register(id, condition);
	}

	@Nullable
	public static <T extends RecipeCondition> T get(Identifier id) {
		return RecipeConditionsImpl.get(id);
	}

	@Nullable
	public static <T extends RecipeCondition> Identifier getId(T condition) {
		return RecipeConditionsImpl.getId(condition);
	}

	public static boolean evaluate(Identifier recipeId, JsonElement element) {
		if (!element.isJsonObject()) throw new IllegalArgumentException("Recipe element is not an object!");
		return RecipeConditionsImpl.evaluate(recipeId, element.getAsJsonObject());
	}
}
