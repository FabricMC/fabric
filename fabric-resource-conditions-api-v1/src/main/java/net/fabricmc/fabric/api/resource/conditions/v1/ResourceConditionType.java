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

package net.fabricmc.fabric.api.resource.conditions.v1;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import com.mojang.serialization.MapCodec;

import net.minecraft.util.Identifier;

public interface ResourceConditionType<T extends ResourceCondition> {
	Codec<ResourceConditionType<?>> TYPE_CODEC = Identifier.CODEC.comapFlatMap(id ->
		Optional.ofNullable(ResourceConditions.getConditionType(id)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown resource condition key: "+ id )),
		ResourceConditionType::id
	);

	Identifier id();
	MapCodec<T> codec();
	static <T extends ResourceCondition> ResourceConditionType<T> create(Identifier id, MapCodec<T> codec) {
		return new ResourceConditionType<>() {
			@Override
			public Identifier id() {
				return id;
			}

			@Override
			public MapCodec<T> codec() {
				return codec;
			}
		};
	}
}
