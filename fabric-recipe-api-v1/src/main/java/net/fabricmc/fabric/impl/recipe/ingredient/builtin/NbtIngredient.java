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
import java.util.Objects;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class NbtIngredient implements CustomIngredient {
	public static final CustomIngredientSerializer<NbtIngredient> SERIALIZER = new Serializer();

	private final Ingredient base;
	@Nullable
	private final NbtCompound nbt;
	private final boolean strict;

	public NbtIngredient(Ingredient base, @Nullable NbtCompound nbt, boolean strict) {
		if (nbt == null && !strict) {
			throw new IllegalArgumentException("NbtIngredient can only have null NBT in strict mode");
		}

		this.base = base;
		this.nbt = nbt;
		this.strict = strict;
	}

	@Override
	public boolean test(ItemStack stack) {
		if (!base.test(stack)) return false;

		if (strict) {
			return Objects.equals(nbt, stack.getNbt());
		} else {
			return NbtHelper.matches(nbt, stack.getNbt(), true);
		}
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		List<ItemStack> stacks = new ArrayList<>(List.of(base.getMatchingStacks()));
		stacks.replaceAll(stack -> {
			ItemStack copy = stack.copy();

			if (nbt != null) {
				copy.setNbt(nbt.copy());
			}

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

	private static class Serializer implements CustomIngredientSerializer<NbtIngredient> {
		private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		private final Identifier id = new Identifier("fabric", "nbt");

		@Override
		public Identifier getIdentifier() {
			return id;
		}

		@Override
		public NbtIngredient read(JsonObject json) {
			Ingredient base = Ingredient.fromJson(json.get("base"));
			NbtCompound nbt = readNbt(json.get("nbt"));
			boolean strict = JsonHelper.getBoolean(json, "strict", false);
			return new NbtIngredient(base, nbt, strict);
		}

		/**
		 * Inspiration taken from {@link NbtPredicate#fromJson}.
		 */
		@Nullable
		private static NbtCompound readNbt(@Nullable JsonElement json) {
			// Process null
			if (json == null || json.isJsonNull()) {
				return null;
			}

			try {
				if (json.isJsonObject()) {
					// We use a normal .toString() to convert the json to string, and read it as SNBT.
					// Using DynamicOps would mess with the type of integers and cause things like damage comparisons to fail...
					return StringNbtReader.parse(json.toString());
				} else {
					// Assume it's a string representation of the NBT
					return StringNbtReader.parse(JsonHelper.asString(json, "nbt"));
				}
			} catch (CommandSyntaxException commandSyntaxException) {
				throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
			}
		}

		@Override
		public void write(JsonObject json, NbtIngredient ingredient) {
			json.add("base", ingredient.base.toJson());
			json.addProperty("strict", ingredient.strict);

			if (ingredient.nbt != null) {
				json.add("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, ingredient.nbt));
			}
		}

		@Override
		public NbtIngredient read(PacketByteBuf buf) {
			Ingredient base = Ingredient.fromPacket(buf);
			NbtCompound nbt = buf.readNbt();
			boolean strict = buf.readBoolean();
			return new NbtIngredient(base, nbt, strict);
		}

		@Override
		public void write(PacketByteBuf buf, NbtIngredient ingredient) {
			ingredient.base.write(buf);
			buf.writeNbt(ingredient.nbt);
			buf.writeBoolean(ingredient.strict);
		}
	}
}
