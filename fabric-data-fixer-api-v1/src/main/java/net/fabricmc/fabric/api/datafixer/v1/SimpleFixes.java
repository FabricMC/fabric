/*
 * Copyright (c) 2016-2022 FabricMC
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
 *
 * This file is a modified version of Quilt Standard Libraries,
 * authored by QuiltMC.
 */

package net.fabricmc.fabric.api.datafixer.v1;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import org.jetbrains.annotations.NotNull;

import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockNameFix;
import net.minecraft.datafixer.fix.EntityRenameFix;
import net.minecraft.datafixer.fix.GameEventRenamesFix;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Identifier;

/**
 * Provides methods to add common {@link DataFix}es to {@link DataFixerBuilder}s.
 */
public final class SimpleFixes {
	// From QSL.
	private SimpleFixes() {
		throw new RuntimeException("SimpleFixes contains only static declarations.");
	}

	/**
	 * Adds a block rename fix to the builder, in case a block's identifier is changed.
	 *
	 * @param builder the builder
	 * @param name    the fix's name
	 * @param oldId   the block's old identifier
	 * @param newId   the block's new identifier
	 * @param schema  the schema this fixer should be a part of
	 * @see BlockNameFix
	 */
	public static void addBlockRenameFix(DataFixerBuilder builder, String name,
			Identifier oldId, Identifier newId,
			Schema schema) {
		Objects.requireNonNull(builder, "DataFixerBuilder cannot be null");
		Objects.requireNonNull(name, "Fix name cannot be null");
		Objects.requireNonNull(oldId, "Old identifier cannot be null");
		Objects.requireNonNull(newId, "New identifier cannot be null");
		Objects.requireNonNull(schema, "Schema cannot be null");

		final String oldIdStr = oldId.toString(), newIdStr = newId.toString();
		builder.addFixer(BlockNameFix.create(schema, name, (inputName) ->
				Objects.equals(IdentifierNormalizingSchema.normalize(inputName), oldIdStr) ? newIdStr : inputName));
	}

	/**
	 * Adds an entity rename fix to the builder, in case an entity's identifier is changed.
	 *
	 * @param builder the builder
	 * @param name    the fix's name
	 * @param oldId   the entity's old identifier
	 * @param newId   the entity's new identifier
	 * @param schema  the schema this fix should be a part of
	 * @see EntityRenameFix
	 */
	public static void addEntityRenameFix(@NotNull DataFixerBuilder builder, @NotNull String name,
			@NotNull Identifier oldId, @NotNull Identifier newId,
			@NotNull Schema schema) {
		requireNonNull(builder, "DataFixerBuilder cannot be null");
		requireNonNull(name, "Fix name cannot be null");
		requireNonNull(oldId, "Old identifier cannot be null");
		requireNonNull(newId, "New identifier cannot be null");
		requireNonNull(schema, "Schema cannot be null");

		final String oldIdStr = oldId.toString(), newIdStr = newId.toString();
		builder.addFixer(new EntityRenameFix(name, schema, false) {
			@Override
			protected String rename(String inputName) {
				return Objects.equals(IdentifierNormalizingSchema.normalize(inputName), oldIdStr) ? newIdStr : inputName;
			}
		});
	}

	/**
	 * Adds an item rename fix to the builder, in case an item's identifier is changed.
	 *
	 * @param builder the builder
	 * @param name    the fix's name
	 * @param oldId   the item's old identifier
	 * @param newId   the item's new identifier
	 * @param schema  the schema this fix should be a part of
	 * @see ItemNameFix
	 */
	public static void addItemRenameFix(DataFixerBuilder builder, String name,
			Identifier oldId, Identifier newId,
			Schema schema) {
		Objects.requireNonNull(builder, "DataFixerBuilder cannot be null");
		Objects.requireNonNull(name, "Fix name cannot be null");
		Objects.requireNonNull(oldId, "Old identifier cannot be null");
		Objects.requireNonNull(newId, "New identifier cannot be null");
		Objects.requireNonNull(schema, "Schema cannot be null");

		final String oldIdStr = oldId.toString(), newIdStr = newId.toString();
		builder.addFixer(ItemNameFix.create(schema, name, (inputName) ->
				Objects.equals(IdentifierNormalizingSchema.normalize(inputName), oldIdStr) ? newIdStr : inputName));
	}

	/**
	 * Adds a biome rename fix to the builder, in case biome identifiers are changed.
	 *
	 * @param builder the builder
	 * @param name    the fix's name
	 * @param changes a map of old biome identifiers to new biome identifiers
	 * @param schema  the schema this fixer should be a part of
	 * @see GameEventRenamesFix
	 */
	public static void addBiomeRenameFix(DataFixerBuilder builder, String name,
			Map<Identifier, Identifier> changes,
			Schema schema) {
		Objects.requireNonNull(builder, "DataFixerBuilder cannot be null");
		Objects.requireNonNull(name, "Fix name cannot be null");
		Objects.requireNonNull(changes, "Changes cannot be null");
		Objects.requireNonNull(schema, "Schema cannot be null");

		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();

		for (Map.Entry<Identifier, Identifier> entry : changes.entrySet()) {
			mapBuilder.put(entry.getKey().toString(), entry.getValue().toString());
		}

		builder.addFixer(new GameEventRenamesFix(schema, name, TypeReferences.BIOME, Schemas.replacing(mapBuilder.build())));
	}
}
