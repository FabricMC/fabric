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

package net.fabricmc.fabric.api.recipe.v1.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientImpl;

/**
 * Serializer for a {@link CustomIngredient}.
 *
 * <p>All instances must be registered using {@link #register} for deserialization to work.
 *
 * @param <T> the type of the custom ingredient
 */
public interface CustomIngredientSerializer<T extends CustomIngredient> {
	/**
	 * Registers a custom ingredient serializer, using the {@linkplain CustomIngredientSerializer#getIdentifier() serializer's identifier}.
	 *
	 * @throws IllegalArgumentException if the serializer is already registered
	 */
	static void register(CustomIngredientSerializer<?> serializer) {
		CustomIngredientImpl.registerSerializer(serializer);
	}

	/**
	 * {@return the custom ingredient serializer registered with the given identifier, or {@code null} if there is no such serializer}.
	 */
	@Nullable
	static CustomIngredientSerializer<?> get(Identifier identifier) {
		return CustomIngredientImpl.getSerializer(identifier);
	}

	/**
	 * {@return the identifier of this serializer}.
	 */
	Identifier getIdentifier();

	/**
	 * Deserializes the custom ingredient from a JSON object.
	 *
	 * @throws JsonSyntaxException if the JSON object does not match the format expected by the serializer
	 * @throws IllegalArgumentException if the JSON object is invalid for some other reason
	 */
	T read(JsonObject json);

	/**
	 * Serializes the custom ingredient to a JSON object.
	 */
	void write(JsonObject json, T ingredient);

	/**
	 * Deserializes the custom ingredient from a packet buffer.
	 */
	T read(PacketByteBuf buf);

	/**
	 * Serializes the custom ingredient to a packet buffer.
	 */
	void write(PacketByteBuf buf, T ingredient);
}
