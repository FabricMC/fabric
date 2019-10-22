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

package net.fabricmc.fabric.impl.datafixer;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.api.datafixer.v1.TypeReferenceHelper;
import net.fabricmc.fabric.impl.datafixer.mixin.Schema100Accessor;
import net.fabricmc.fabric.impl.datafixer.mixin.Schema99Accessor;

public final class TypeRefHelperImpl implements TypeReferenceHelper {
	public static final TypeRefHelperImpl INSTANCE = new TypeRefHelperImpl();
	
	private TypeRefHelperImpl() {}

	@Override
	public void registerTypeInTile(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		Schema99Accessor.callMethod_5368(schema, typeMap, name); // registerTypeInTile
	}

	@Override
	public void registerTypeWithEquipment(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		Schema99Accessor.callMethod_5339(schema, typeMap, name); // registerTypeWithEquipment
	}

	@Override
	public void registerSimpleType(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		schema.registerSimple(typeMap, name);
	}

	@Override
	public void registerTypeWithItems(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		Schema99Accessor.callMethod_5346(schema, typeMap, name); // registerTypeWithItems
	}

	@Override
	public void registerTypeWithArmorAndToolSlots(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		Schema100Accessor.callMethod_5195(schema, typeMap, name);
	}

	@Override
	public void registerTypeWithTemplate(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, Supplier<TypeTemplate> typeTemplateSupplier, String name) {
		schema.register(typeMap, name, typeTemplateSupplier);
	}

	// TODO: Scavenge other Schema classes for DSL helper methods
}
