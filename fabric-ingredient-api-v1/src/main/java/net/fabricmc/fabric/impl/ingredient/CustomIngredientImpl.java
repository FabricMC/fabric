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

package net.fabricmc.fabric.impl.ingredient;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.ingredient.v1.CustomIngredient;
import net.fabricmc.fabric.api.ingredient.v1.CustomIngredientSerializer;
import net.fabricmc.fabric.impl.ingredient.builtin.AndIngredient;
import net.fabricmc.fabric.impl.ingredient.builtin.DifferenceIngredient;
import net.fabricmc.fabric.impl.ingredient.builtin.NbtIngredient;
import net.fabricmc.fabric.impl.ingredient.builtin.OrIngredient;

@ApiStatus.Internal
public class CustomIngredientImpl extends Ingredient {
	// Static helpers used by the API

	public static final String TYPE_KEY = "fabric:type";
	public static final int PACKET_MARKER = -1;

	private static final Map<Identifier, CustomIngredientSerializer<?>> REGISTERED_SERIALIZERS = new ConcurrentHashMap<>();

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

	static {
		CustomIngredientSerializer.register(AndIngredient.SERIALIZER);
		CustomIngredientSerializer.register(OrIngredient.SERIALIZER);
		CustomIngredientSerializer.register(DifferenceIngredient.SERIALIZER);
		CustomIngredientSerializer.register(NbtIngredient.SERIALIZER);
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
			this.matchingStacks = customIngredient.getMatchingStacks();
		}

		return this.matchingStacks;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && customIngredient.test(stack);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(TYPE_KEY, customIngredient.getSerializer().getIdentifier().toString());
		((CustomIngredientSerializer) customIngredient.getSerializer()).write(json, customIngredient);
		return json;
	}

	@Override
	public boolean isEmpty() {
		// We don't want to resolve the matching stacks,
		// as this might cause the ingredient to use outdated tags when it's done too early.
		// So we just return false when the matching stacks haven't been resolved yet (i.e. when the field is null).
		// TODO: this is a bit hacky, can we not do better?
		return matchingStacks != null && matchingStacks.length == 0;
	}
}
