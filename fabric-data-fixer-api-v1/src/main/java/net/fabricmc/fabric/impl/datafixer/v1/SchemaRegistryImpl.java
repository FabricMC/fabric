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
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.templates.TypeTemplate;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datafixer.v1.SchemaRegistry;

public class SchemaRegistryImpl implements SchemaRegistry {
	private final Map<String, Supplier<TypeTemplate>> registry = new Object2ReferenceOpenHashMap<>();

	@Override
	public void register(Identifier id, Supplier<TypeTemplate> template) {
		this.registry.put(id.toString(), template);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> get() {
		return ImmutableMap.copyOf(this.registry);
	}

	@Override
	public List<String> getKeys() {
		return ImmutableList.copyOf(this.registry.keySet());
	}

	@Override
	public List<Supplier<TypeTemplate>> getValues() {
		return ImmutableList.copyOf(this.registry.values());
	}
}
