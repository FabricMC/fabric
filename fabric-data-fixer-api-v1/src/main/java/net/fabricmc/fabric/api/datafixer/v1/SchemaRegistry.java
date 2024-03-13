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

package net.fabricmc.fabric.api.datafixer.v1;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;

import net.minecraft.util.Identifier;

public interface SchemaRegistry {
	void register(Identifier id, Supplier<TypeTemplate> template);

	void register(Identifier id, Function<String, TypeTemplate> template);

	void addSchema(BiFunction<Integer, Schema, Schema> factory);

	Supplier<TypeTemplate> remove(Identifier id);

	ImmutableMap<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> get();

	ImmutableList<String> getKeys();

	ImmutableList<Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> getValues();

	ImmutableList<BiFunction<Integer, Schema, Schema>> getFutureSchemas();
}
