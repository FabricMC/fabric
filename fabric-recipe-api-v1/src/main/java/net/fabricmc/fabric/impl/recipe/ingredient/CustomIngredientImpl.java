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

package net.fabricmc.fabric.impl.recipe.ingredient;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

/**
 * To test this API beyond the unit tests, please refer to the recipe provider in the datagen API testmod.
 * It contains various interesting recipes to test, and explains how to package them in a datapack.
 */
public class CustomIngredientImpl extends Ingredient {
	// Static helpers used by the API

	public static final String TYPE_KEY = "fabric:type";

	static final Map<Identifier, CustomIngredientSerializer<?>> REGISTERED_SERIALIZERS = new ConcurrentHashMap<>();

	public static final Codec<CustomIngredientSerializer<?>> CODEC = Identifier.CODEC.flatXmap(identifier ->
					Optional.ofNullable(REGISTERED_SERIALIZERS.get(identifier))
							.map(DataResult::success)
							.orElseGet(() -> DataResult.error(() -> "Unknown custom ingredient serializer: " + identifier)),
			serializer -> DataResult.success(serializer.getIdentifier())
	);

	public static void registerSerializer(CustomIngredientSerializer<?> serializer) {
		Objects.requireNonNull(serializer.getIdentifier(), "CustomIngredientSerializer identifier may not be null.");

		if (REGISTERED_SERIALIZERS.putIfAbsent(serializer.getIdentifier(), serializer) != null) {
			throw new IllegalArgumentException("CustomIngredientSerializer with identifier " + serializer.getIdentifier() + " already registered.");
		}
	}

	@Nullable
	public static CustomIngredientSerializer<?> getSerializer(Identifier identifier) {
		Objects.requireNonNull(identifier, "Identifier may not be null.");

		return REGISTERED_SERIALIZERS.get(identifier);
	}

	// Actual custom ingredient logic

	private final CustomIngredient customIngredient;

	public CustomIngredientImpl(CustomIngredient customIngredient) {
		super(Stream.empty());

		this.customIngredient = customIngredient;
	}

	@Override
	public CustomIngredient getCustomIngredient() {
		return customIngredient;
	}

	@Override
	public boolean requiresTesting() {
		return customIngredient.requiresTesting();
	}

	@Override
	public ItemStack[] getMatchingStacks() {
		if (this.matchingStacks == null) {
			this.matchingStacks = customIngredient.getMatchingStacks().toArray(ItemStack[]::new);
		}

		return this.matchingStacks;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && customIngredient.test(stack);
	}

	@Override
	public boolean isEmpty() {
		// We don't want to resolve the matching stacks,
		// as this might cause the ingredient to use outdated tags when it's done too early.
		// So we just return false when the matching stacks haven't been resolved yet (i.e. when the field is null).
		return matchingStacks != null && matchingStacks.length == 0;
	}
}
