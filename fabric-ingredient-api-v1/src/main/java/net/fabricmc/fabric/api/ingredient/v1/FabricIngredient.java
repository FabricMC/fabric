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

package net.fabricmc.fabric.api.ingredient.v1;

import org.jetbrains.annotations.Nullable;

import net.minecraft.recipe.Ingredient;

/**
 * Fabric-provided extensions for {@link Ingredient}.
 * This interface is automatically implemented on all ingredients via Mixin and interface injection.
 */
public interface FabricIngredient {
	/**
	 * {@return the backing {@link CustomIngredient} of this ingredient if it's custom, {@code null} otherwise}.
	 */
	@Nullable
	default CustomIngredient getCustomIngredient() {
		return null;
	}

	/**
	 * @see CustomIngredient#requiresTesting()
	 */
	default boolean requiresTesting() {
		return getCustomIngredient() != null && getCustomIngredient().requiresTesting();
	}
}
