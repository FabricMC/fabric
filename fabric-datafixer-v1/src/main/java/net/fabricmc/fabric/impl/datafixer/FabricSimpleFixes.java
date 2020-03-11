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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixer.fix.BlockNameFix;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.datafixer.schema.SchemaIdentifierNormalize;

import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes;

public class FabricSimpleFixes implements SimpleFixes {
	public static final FabricSimpleFixes INSTANCE = new FabricSimpleFixes();

	private FabricSimpleFixes() { }

	@Override
	public void addBlockRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema) {
		checkNotNull(builder, "DataFixer builder cannot be null");
		checkNotNull(name, "Fix name cannot be null");
		checkNotNull(oldId, "Old id cannot be null");
		checkNotNull(newId, "New id cannot be null");
		checkNotNull(schema, "Schema cannot be null");
		builder.addFixer(BlockNameFix.create(schema, name, (inputName) -> {
			return Objects.equals(SchemaIdentifierNormalize.normalize(inputName), oldId) ? newId : inputName;
		}));
	}

	@Override
	public void addItemRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema) {
		checkNotNull(builder, "DataFixer builder cannot be null");
		checkNotNull(name, "Fix name cannot be null");
		checkNotNull(oldId, "Old id cannot be null");
		checkNotNull(newId, "New id cannot be null");
		checkNotNull(schema, "Schema cannot be null");
		builder.addFixer(ItemNameFix.create(schema, name, (inputName) -> {
			return Objects.equals(oldId, inputName) ? newId : inputName;
		}));
	}

	@Override
	public void addBiomeRenameFix(DataFixerBuilder builder, String name, ImmutableMap<String, String> changes, Schema schema) {
		checkNotNull(builder, "DataFixer builder cannot be null");
		checkNotNull(name, "Fix name cannot be null");
		checkNotNull(changes, "Changes cannot be null");
		checkNotNull(schema, "Schema cannot be null");
		builder.addFixer(new BiomeNameFix(schema, false, name, changes));
	}
}
