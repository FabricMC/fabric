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

package net.fabricmc.fabric.impl.datafixer.v1;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datafixer.v1.SchemaRegistry;

public class SchemaRegistryImpl implements SchemaRegistry {
	private final Map<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> registry = new Object2ReferenceOpenHashMap<>();
	private final List<BiFunction<Integer, Schema, Schema>> subVersionSchemas = new ObjectArrayList<>();

	@Override
	public void register(Identifier id, Supplier<TypeTemplate> template) {
		this.registry.put(id.toString(), Either.left(template));
	}

	@Override
	public void register(Identifier id, Function<String, TypeTemplate> template) {
		this.registry.put(id.toString(), Either.right(template));
	}

	@Override
	public void addSchema(BiFunction<Integer, Schema, Schema> factory) {
		this.subVersionSchemas.add(factory);
	}

	@Override
	public Supplier<TypeTemplate> remove(Identifier id) {
		Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>> found = this.registry.get(id.toString());
		AtomicReference<Supplier<TypeTemplate>> supplier = new AtomicReference<>();

		found.ifLeft(supplier::set);
		found.ifRight(function -> supplier.set(() -> function.apply(id.toString())));

		this.registry.remove(id.toString());
		return supplier.get();
	}

	@Override
	public ImmutableMap<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> get() {
		return ImmutableMap.copyOf(this.registry);
	}

	@Override
	public ImmutableList<String> getKeys() {
		return ImmutableList.copyOf(this.registry.keySet());
	}

	@Override
	public ImmutableList<Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> getValues() {
		return ImmutableList.copyOf(this.registry.values());
	}

	@Override
	public ImmutableList<BiFunction<Integer, Schema, Schema>> getFutureSchemas() {
		return ImmutableList.copyOf(this.subVersionSchemas);
	}
}
