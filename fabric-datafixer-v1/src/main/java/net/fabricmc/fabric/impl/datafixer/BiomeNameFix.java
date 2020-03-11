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
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;

import net.minecraft.datafixer.TypeReferences;

// TODO in 1.16, this can be replaced with a call to BiomeRenameFix
public class BiomeNameFix extends DataFix {
	private String name;
	private Map<String, String> changes;

	public BiomeNameFix(Schema outputSchema, boolean changesType, String name, ImmutableMap<String, String> changes) {
		super(outputSchema, changesType);
		this.name = name;
		this.changes = changes;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		// First we must get the type represented by the biome. We do this and get a NamedType in return
		Type<Pair<String, String>> biomeType = DSL.named(TypeReferences.BIOME.typeName(), DSL.namespacedString());

		// Next we must make sure the input type matches the biome or else data corruption could occur.
		if (!Objects.equals(biomeType, this.getInputSchema().getType(TypeReferences.BIOME))) {
			throw new IllegalStateException("Biome type is not what was expected.");
		}

		return this.fixTypeEverywhere(name, biomeType, (ops) -> {
			return (pair) -> {
				return pair.mapSecond((name) -> {
					return changes.getOrDefault(name, name);
				});
			};
		});
	}
}
