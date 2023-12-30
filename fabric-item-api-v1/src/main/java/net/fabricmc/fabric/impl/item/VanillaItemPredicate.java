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

package net.fabricmc.fabric.impl.item;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registry;

import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;

public record VanillaItemPredicate(ItemPredicate delegate) implements CustomItemPredicate {
	private static Codec<VanillaItemPredicate> codec;

	public VanillaItemPredicate {
		Preconditions.checkArgument(delegate.custom().isEmpty(), "The delegate shouldn't have custom predicates");
	}

	@Override
	public boolean test(ItemStack stack) {
		return delegate.test(stack);
	}

	@Override
	public Codec<? extends CustomItemPredicate> getCodec() {
		return codec;
	}

	public static void register(Codec<ItemPredicate> vanillaCodec) {
		// Calling xmap directly on the Codec will result it getting wrapped in a "value" object when dispatched.
		// See https://github.com/Mojang/DataFixerUpper/pull/83 for more details.
		codec = ((MapCodec.MapCodecCodec<ItemPredicate>) vanillaCodec).codec().xmap(VanillaItemPredicate::new, VanillaItemPredicate::delegate).codec();

		Registry.register(REGISTRY, "default", codec);
	}
}
