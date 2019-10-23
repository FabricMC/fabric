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

package net.fabricmc.fabric.impl.datafixer.test;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerEntrypoint;
import net.fabricmc.fabric.api.datafixer.v1.TypeReferenceHelper;
import net.minecraft.datafixers.TypeReferences;

import java.util.Map;
import java.util.function.Supplier;

public class TestDataFixerEntrypoint implements DataFixerEntrypoint {
	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap) {
		TypeReferenceHelper.HELPER.registerSimpleType(schema, entityMap, "TestEntity");
		return entityMap;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema, Map<String, Supplier<TypeTemplate>> blockEntityMap) {
		TypeReferenceHelper.HELPER.registerSimpleType(schema, blockEntityMap, "test:testblockentity"); // Changed later, kept for legacy mode

		TypeReferenceHelper.HELPER.registerTypeWithTemplate(schema, blockEntityMap, () -> DSL.optionalFields(
			"Left", DSL.list(TypeReferences.ITEM_STACK.in(schema)),
			"Right", DSL.list(TypeReferences.ITEM_STACK.in(schema))), "spookytime:tiny_pumpkin");

		TypeReferenceHelper.HELPER.registerSimpleType(schema, blockEntityMap, "test:testblockentity2"); // TODO changed

		return blockEntityMap;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
	}
}
