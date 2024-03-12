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

package net.fabricmc.fabric.test.lookup;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.datafixer.schema.Schema100;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datafixer.v1.DataFixerEntrypoint;

public class FabricApiLookupTestDataFixer implements DataFixerEntrypoint {
	@Override
	public void onRegisterBlockEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema) {
	}

	@Override
	public void onRegisterEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema) {
		schema.register(
				registry,
				new Identifier(FabricApiLookupTest.MOD_ID, "inspectable_pig").toString(),
				() -> Schema100.targetItems(schema)
		);
	}
}
