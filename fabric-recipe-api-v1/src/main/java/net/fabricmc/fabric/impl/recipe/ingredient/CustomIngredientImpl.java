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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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
	public static final int PACKET_MARKER = -1;

	static final Map<Identifier, CustomIngredientSerializer<?>> REGISTERED_SERIALIZERS = new ConcurrentHashMap<>();

	public static final Codec<CustomIngredientSerializer<?>> CODEC = Identifier.CODEC.flatXmap(identifier ->
					Optional.ofNullable(REGISTERED_SERIALIZERS.get(identifier))
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error(() -> "Unknown custom ingredient serializer: " + identifier)),
			serializer -> DataResult.success(serializer.getIdentifier())
	);

	public static final Codec<CustomIngredient> ALLOW_EMPTY_INGREDIENT_CODECS = CODEC.dispatch(TYPE_KEY, CustomIngredient::getSerializer, serializer -> serializer.getCodec(true));
	public static final Codec<CustomIngredient> DISALLOW_EMPTY_INGREDIENT_CODECS = CODEC.dispatch(TYPE_KEY, CustomIngredient::getSerializer, serializer -> serializer.getCodec(false));

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
	public void write(PacketByteBuf buf) {
		// Can be null if we're not writing a packet from the PacketEncoder; in that case, always write the full ingredient.
		// Chances are this is a mod's doing and the client has the Ingredient API with the relevant ingredients.
		Set<Identifier> supportedIngredients = CustomIngredientSync.CURRENT_SUPPORTED_INGREDIENTS.get();

		if (supportedIngredients != null && !supportedIngredients.contains(customIngredient.getSerializer().getIdentifier())) {
			// The client doesn't support this custom ingredient, so we send the matching stacks as a regular ingredient.
			// Conveniently, this is exactly what the super call does.
			super.write(buf);
		} else {
			// The client supports this custom ingredient, so we send it as a custom ingredient.
			buf.writeVarInt(PACKET_MARKER);
			buf.writeIdentifier(customIngredient.getSerializer().getIdentifier());
			customIngredient.getSerializer().write(buf, coerceIngredient());
		}
	}

	@Override
	public boolean isEmpty() {
		// We don't want to resolve the matching stacks,
		// as this might cause the ingredient to use outdated tags when it's done too early.
		// So we just return false when the matching stacks haven't been resolved yet (i.e. when the field is null).
		return matchingStacks != null && matchingStacks.length == 0;
	}

	private <T> T coerceIngredient() {
		return (T) customIngredient;
	}

	public static <T> Codec<T> first(Codec<T> first, Codec<T> second) {
		return new First<>(first, second);
	}

	// Decode/encode the first codec, if that fails return the result of the second.
	record First<T>(Codec<T> first, Codec<T> second) implements Codec<T> {
		@Override
		public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
			DataResult<Pair<T, T1>> firstResult = first.decode(ops, input);

			if (firstResult.result().isPresent()) {
				return firstResult;
			}

			return second.decode(ops, input);
		}

		@Override
		public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
			DataResult<T1> firstResult = first.encode(input, ops, prefix);

			if (firstResult.result().isPresent()) {
				return firstResult;
			}

			return second.encode(input, ops, prefix);
		}
	}
}
