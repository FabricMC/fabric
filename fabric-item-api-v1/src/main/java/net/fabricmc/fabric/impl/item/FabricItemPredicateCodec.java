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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RuntimeOps;

import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;

public final class FabricItemPredicateCodec extends MapCodec<ItemPredicate> {
	public static final BiMap<Identifier, Codec<CustomItemPredicate>> REGISTRY = HashBiMap.create();

	public static Codec<ItemPredicate> vanillaCodec;
	public static Codec<ItemPredicate> customCodec;

	private final MapCodec<ItemPredicate> vanilla;
	private final Set<String> vanillaKeys;

	public static Codec<ItemPredicate> init(Codec<ItemPredicate> vanilla) {
		vanillaCodec = vanilla;
		customCodec = new FabricItemPredicateCodec(vanilla).codec();
		return customCodec;
	}

	private FabricItemPredicateCodec(Codec<ItemPredicate> vanilla) {
		this.vanilla = ((MapCodecCodec<ItemPredicate>) vanilla).codec();
		this.vanillaKeys = this.vanilla.keys(RuntimeOps.INSTANCE).map(Object::toString).collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public <T> Stream<T> keys(DynamicOps<T> ops) {
		Stream<T> custom = REGISTRY.keySet().stream().map(id -> ops.createString(id.toString()));
		return Stream.concat(vanilla.keys(ops), custom);
	}

	@Override
	public <T> DataResult<ItemPredicate> decode(DynamicOps<T> ops, MapLike<T> input) {
		return vanilla.decode(ops, input).flatMap(predicate -> {
			MutableObject<DataResult<ItemPredicate>> result = new MutableObject<>(DataResult.success(predicate));
			ImmutableList.Builder<CustomItemPredicate> customs = new ImmutableList.Builder<>();

			input.entries().forEach(entry -> {
				if (result.getValue().error().isPresent()) return;

				result.setValue(Identifier.CODEC.decode(ops, entry.getFirst()).flatMap(key -> {
					Identifier customId = key.getFirst();

					if (customId.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) && vanillaKeys.contains(customId.getPath())) {
						return DataResult.success(predicate);
					}

					@Nullable Codec<CustomItemPredicate> customCodec = REGISTRY.get(customId);
					if (customCodec == null) return DataResult.error(() -> "Unknown custom predicate id " + customId);

					return customCodec.decode(ops, entry.getSecond()).map(p -> {
						customs.add(p.getFirst());
						return predicate;
					});
				}));
			});

			return result.getValue().map(p -> {
				((ItemPredicateExtensions) (Object) p).fabric_setCustom(customs.build());
				return p;
			});
		});
	}

	@Override
	public <T> RecordBuilder<T> encode(ItemPredicate input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
		RecordBuilder<T> builder = vanilla.encode(input, ops, prefix);

		for (CustomItemPredicate custom : input.custom()) {
			@SuppressWarnings("unchecked")
			Codec<CustomItemPredicate> customCodec = (Codec<CustomItemPredicate>) custom.getCodec();

			String customId = REGISTRY.inverse().get(customCodec).toString();
			builder.add(customId, customCodec.encodeStart(ops, custom));
		}

		return builder;
	}
}
