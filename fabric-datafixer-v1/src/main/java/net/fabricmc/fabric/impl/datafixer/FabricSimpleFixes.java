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

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes;
import net.fabricmc.fabric.impl.datafixer.fixes.BiomeRenameFix;
import net.fabricmc.fabric.impl.datafixer.fixes.BlockEntityRenameFix;
import net.fabricmc.fabric.impl.datafixer.fixes.EntityTransformationFixWrapper;
import net.minecraft.datafixers.fixes.BlockNameFix;
import net.minecraft.datafixers.fixes.EntityRenameFix;
import net.minecraft.datafixers.fixes.EntitySimpleTransformFix;
import net.minecraft.datafixers.fixes.FixItemName;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import net.minecraft.nbt.Tag;

public final class FabricSimpleFixes implements SimpleFixes {
	public static final SimpleFixes INSTANCE = new FabricSimpleFixes();

	private FabricSimpleFixes() {}

	@Override
	public void addBlockRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema) {
		validateFixArgs(builder, name, oldId, newId, schema);

		builder.addFixer(BlockNameFix.create(schema, name, (inputBlockName) -> {
			return Objects.equals(SchemaIdentifierNormalize.normalize(inputBlockName), oldId) ? newId : inputBlockName;
		}));
	}

	@Override
	public void addItemRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema) {
		validateFixArgs(builder, name, oldId, newId, schema);

		builder.addFixer(FixItemName.create(schema, name, (inputItemName) -> {
			return Objects.equals(oldId, inputItemName) ? newId : inputItemName;
		}));
	}

	@Override
	public void addEntityRenameFix(DataFixerBuilder builder_1, String name, String oldId, String newId, Schema schema_1) {
		builder_1.addFixer(new EntityRenameFix(name, schema_1, false) {
			@Override
			protected String rename(String inputName) {
				return Objects.equals(oldId, inputName) ? newId : inputName;
			}
		});

	}

	@Override
	public void addBiomeRenameFix(DataFixerBuilder builder, String name, ImmutableMap<String, String> changes, Schema schema) {
		builder.addFixer(new BiomeRenameFix(schema, false, name, changes));
	}

	@Override
	public void addEntityTransformFix(DataFixerBuilder builder, String name, EntityTransformation transformation, Schema schema_1) {
		builder.addFixer(new EntityTransformationFixWrapper(name, schema_1, false, transformation));
	}

	/**
	 * Needs testing before release.
	 */
	@Override
	public void addBlockEntityRenameFix(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema) {
		validateFixArgs(builder, name, oldId, newId, schema);

		builder.addFixer(new BlockEntityRenameFix(schema, false, oldId) {

			@Override
			protected String rename(String inputString) {
				return Objects.equals(inputString, oldId) ? newId : inputString;
			}

		});
	}

	private static void validateFixArgs(DataFixerBuilder builder, String name, String oldId, String newId, Schema schema) {
		Preconditions.checkNotNull(schema, "Schema cannot be null");
		Preconditions.checkNotNull(name, "Name cannot be null");
		Preconditions.checkNotNull(oldId, "Old id cannot be null");
		Preconditions.checkNotNull(newId, "New id cannot be null");
		Preconditions.checkNotNull(builder, "DataFixerBuilder cannot be null");
	}
}
