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

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

import net.fabricmc.fabric.api.datafixer.v1.SchemaRegistry;

public class FabricSchema1903 extends IdentifierNormalizingSchema {
	public SchemaRegistry registeredBlockEntities;
	public SchemaRegistry registeredEntities;

	public FabricSchema1903(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

		this.registeredBlockEntities = new SchemaRegistryImpl();
		FabricDataFixesInternals.registerBlockEntities(this.registeredBlockEntities, schema);

		Map<String, Supplier<TypeTemplate>> modRegistry = this.registeredBlockEntities.get();

		for (Map.Entry<String, Supplier<TypeTemplate>> entry : modRegistry.entrySet()) {
			schema.register(map, entry.getKey(), entry.getValue());
		}

		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

		this.registeredEntities = new SchemaRegistryImpl();
		FabricDataFixesInternals.registerEntities(this.registeredEntities, schema);

		Map<String, Supplier<TypeTemplate>> modRegistry = this.registeredEntities.get();

		for (Map.Entry<String, Supplier<TypeTemplate>> entry : modRegistry.entrySet()) {
			schema.register(map, entry.getKey(), entry.getValue());
		}

		return map;
	}
}
