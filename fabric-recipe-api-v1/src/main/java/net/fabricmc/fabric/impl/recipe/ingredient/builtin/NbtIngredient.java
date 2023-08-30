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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

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

	private Ingredient getBase() {
		return base;
	}

	private NbtCompound getNbt() {
		return nbt;
	}

	private boolean isStrict() {
		return strict;
	}

	private static class Serializer implements CustomIngredientSerializer<NbtIngredient> {
		private static final Identifier ID = new Identifier("fabric", "nbt");
		private static Codec<NbtIngredient> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						Ingredient.field_46095.fieldOf("base").forGetter(NbtIngredient::getBase),
						NbtCompound.CODEC.optionalFieldOf("nbt", null).forGetter(NbtIngredient::getNbt),
						Codec.BOOL.optionalFieldOf("struct", false).forGetter(NbtIngredient::isStrict)
				).apply(instance, NbtIngredient::new)
		);

		@Override
		public Identifier getIdentifier() {
			return ID;
		}

		@Override
		public Codec<NbtIngredient> getCodec() {
			return CODEC;
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
