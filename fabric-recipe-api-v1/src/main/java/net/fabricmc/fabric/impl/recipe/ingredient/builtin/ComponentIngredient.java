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

import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class ComponentIngredient implements CustomIngredient {
	public static final CustomIngredientSerializer<ComponentIngredient> SERIALIZER = new Serializer();

	private final Ingredient base;
	private final ComponentChanges components;
	private final boolean strict;

	public ComponentIngredient(Ingredient base, ComponentChanges components, boolean strict) {
		if (components.isEmpty() && !strict) {
			throw new IllegalArgumentException("ComponentIngredient can only have empty components in strict mode");
		}

		this.base = base;
		this.components = components;
		this.strict = strict;
	}

	@Override
	public boolean test(ItemStack stack) {
		if (!base.test(stack)) return false;

		if (strict) {
			return Objects.equals(components, stack.getComponentChanges());
		} else {
			// TODO 1.20.5
//			return NbtHelper.matches(components, stack.getComponentChanges(), true);
			return false;
		}
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		List<ItemStack> stacks = new ArrayList<>(List.of(base.getMatchingStacks()));
		stacks.replaceAll(stack -> {
			ItemStack copy = stack.copy();

			stack.applyChanges(components);

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

	@Nullable
	private ComponentChanges getComponents() {
		return components;
	}

	private boolean isStrict() {
		return strict;
	}

	private static class Serializer implements CustomIngredientSerializer<ComponentIngredient> {
		private static final Identifier ID = new Identifier("fabric", "component");
		private static final Codec<ComponentIngredient> ALLOW_EMPTY_CODEC = createCodec(Ingredient.ALLOW_EMPTY_CODEC);
		private static final Codec<ComponentIngredient> DISALLOW_EMPTY_CODEC = createCodec(Ingredient.DISALLOW_EMPTY_CODEC);
		private static final PacketCodec<RegistryByteBuf, ComponentIngredient> PACKET_CODEC = PacketCodec.tuple(
				Ingredient.PACKET_CODEC, ComponentIngredient::getBase,
				ComponentChanges.PACKET_CODEC, ComponentIngredient::getComponents,
				PacketCodecs.BOOL, ComponentIngredient::isStrict,
				ComponentIngredient::new
		);

		private static Codec<ComponentIngredient> createCodec(Codec<Ingredient> ingredientCodec) {
			return RecordCodecBuilder.create(instance ->
					instance.group(
							ingredientCodec.fieldOf("base").forGetter(ComponentIngredient::getBase),
							ComponentChanges.CODEC.fieldOf("components").forGetter(ComponentIngredient::getComponents),
							Codec.BOOL.optionalFieldOf("strict", false).forGetter(ComponentIngredient::isStrict)
					).apply(instance, ComponentIngredient::new)
			);
		}

		@Override
		public Identifier getIdentifier() {
			return ID;
		}

		@Override
		public Codec<ComponentIngredient> getCodec(boolean allowEmpty) {
			return allowEmpty ? ALLOW_EMPTY_CODEC : DISALLOW_EMPTY_CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, ComponentIngredient> getPacketCodec() {
			return PACKET_CODEC;
		}
	}
}
