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

package net.fabricmc.fabric.impl.recipe.ingredient.builtin;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class DifferenceIngredient implements CustomIngredient {
	public static final CustomIngredientSerializer<DifferenceIngredient> SERIALIZER = new Serializer();

	private final Ingredient base;
	private final Ingredient subtracted;

	public DifferenceIngredient(Ingredient base, Ingredient subtracted) {
		this.base = base;
		this.subtracted = subtracted;
	}

	@Override
	public boolean test(ItemStack stack) {
		return base.test(stack) && !subtracted.test(stack);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		List<ItemStack> stacks = new ArrayList<>(List.of(base.getMatchingStacks()));
		stacks.removeIf(subtracted);
		return stacks;
	}

	@Override
	public boolean requiresTesting() {
		return base.requiresTesting() || subtracted.requiresTesting();
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private Ingredient getBase() {
		return base;
	}

	private Ingredient getSubtracted() {
		return subtracted;
	}

	private static class Serializer implements CustomIngredientSerializer<DifferenceIngredient> {
		private static final Identifier ID = new Identifier("fabric", "difference");
		private static final Codec<DifferenceIngredient> ALLOW_EMPTY_CODEC = createCodec(Ingredient.ALLOW_EMPTY_CODEC);
		private static final Codec<DifferenceIngredient> DISALLOW_EMPTY_CODEC = createCodec(Ingredient.DISALLOW_EMPTY_CODEC);

		private static Codec<DifferenceIngredient> createCodec(Codec<Ingredient> ingredientCodec) {
			return RecordCodecBuilder.create(instance ->
					instance.group(
							ingredientCodec.fieldOf("base").forGetter(DifferenceIngredient::getBase),
							ingredientCodec.fieldOf("subtracted").forGetter(DifferenceIngredient::getSubtracted)
					).apply(instance, DifferenceIngredient::new)
			);
		}

		@Override
		public Identifier getIdentifier() {
			return ID;
		}

		@Override
		public Codec<DifferenceIngredient> getCodec(boolean allowEmpty) {
			return allowEmpty ? ALLOW_EMPTY_CODEC : DISALLOW_EMPTY_CODEC;
		}

		@Override
		public DifferenceIngredient read(PacketByteBuf buf) {
			Ingredient base = Ingredient.fromPacket(buf);
			Ingredient subtracted = Ingredient.fromPacket(buf);
			return new DifferenceIngredient(base, subtracted);
		}

		@Override
		public void write(PacketByteBuf buf, DifferenceIngredient ingredient) {
			ingredient.base.write(buf);
			ingredient.subtracted.write(buf);
		}
	}
}
