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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class CustomDataIngredient implements CustomIngredient {
	public static final CustomIngredientSerializer<CustomDataIngredient> SERIALIZER = new Serializer();
	private final Ingredient base;
	private final NbtCompound nbt;

	public CustomDataIngredient(Ingredient base, NbtCompound nbt) {
		if (nbt == null || nbt.isEmpty()) throw new IllegalArgumentException("NBT cannot be null; use components ingredient for strict matching");

		this.base = base;
		this.nbt = nbt;
	}

	@Override
	public boolean test(ItemStack stack) {
		if (!base.test(stack)) return false;

		NbtComponent nbt = stack.get(DataComponentTypes.CUSTOM_DATA);

		return nbt != null && nbt.matches(this.nbt);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		List<ItemStack> stacks = new ArrayList<>(List.of(base.getMatchingStacks()));
		stacks.replaceAll(stack -> {
			ItemStack copy = stack.copy();
			copy.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, existingNbt -> NbtComponent.of(existingNbt.copyNbt().copyFrom(this.nbt)));
			return copy;
		});
		stacks.removeIf(stack -> !base.test(stack));
		return stacks;
	}

	@Override
	public boolean requiresTesting() {
		return true;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private Ingredient getBase() {
		return base;
	}

	private NbtCompound getNbt() {
		return nbt;
	}

	private static class Serializer implements CustomIngredientSerializer<CustomDataIngredient> {
		private static final Identifier ID = Identifier.of("fabric", "custom_data");

		private static final MapCodec<CustomDataIngredient> ALLOW_EMPTY_CODEC = createCodec(Ingredient.ALLOW_EMPTY_CODEC);
		private static final MapCodec<CustomDataIngredient> DISALLOW_EMPTY_CODEC = createCodec(Ingredient.DISALLOW_EMPTY_CODEC);

		private static final PacketCodec<RegistryByteBuf, CustomDataIngredient> PACKET_CODEC = PacketCodec.tuple(
				Ingredient.PACKET_CODEC, CustomDataIngredient::getBase,
				PacketCodecs.NBT_COMPOUND, CustomDataIngredient::getNbt,
				CustomDataIngredient::new
		);

		private static MapCodec<CustomDataIngredient> createCodec(Codec<Ingredient> ingredientCodec) {
			return RecordCodecBuilder.mapCodec(instance ->
					instance.group(
							ingredientCodec.fieldOf("base").forGetter(CustomDataIngredient::getBase),
							StringNbtReader.NBT_COMPOUND_CODEC.fieldOf("nbt").forGetter(CustomDataIngredient::getNbt)
					).apply(instance, CustomDataIngredient::new)
			);
		}

		@Override
		public Identifier getIdentifier() {
			return ID;
		}

		@Override
		public MapCodec<CustomDataIngredient> getCodec(boolean allowEmpty) {
			return allowEmpty ? ALLOW_EMPTY_CODEC : DISALLOW_EMPTY_CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, CustomDataIngredient> getPacketCodec() {
			return PACKET_CODEC;
		}
	}
}
