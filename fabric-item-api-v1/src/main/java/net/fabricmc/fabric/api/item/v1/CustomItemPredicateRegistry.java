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

package net.fabricmc.fabric.api.item.v1;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.item.FabricItemPredicateCodec;

public final class CustomItemPredicateRegistry {
	/**
	 * Registers a codec to be used to serialize/deserialize a custom item predicate.
	 *
	 * @param id    the predicate id
	 * @param codec the predicate codec
	 */
	@SuppressWarnings("unchecked")
	public static void register(Identifier id, Codec<? extends CustomItemPredicate> codec) {
		Preconditions.checkArgument(FabricItemPredicateCodec.REGISTRY.containsKey(id), "There's already a codec that registered with this id");

		if (FabricItemPredicateCodec.REGISTRY.containsValue(codec)) {
			throw new IllegalArgumentException("The codec is already registered with id " + FabricItemPredicateCodec.REGISTRY.inverse().get(codec));
		}

		FabricItemPredicateCodec.REGISTRY.put(id, (Codec<CustomItemPredicate>) codec);
	}

	private CustomItemPredicateRegistry() {
	}
}
