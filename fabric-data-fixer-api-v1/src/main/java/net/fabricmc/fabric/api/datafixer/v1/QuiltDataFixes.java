/*
 * Copyright 2022 QuiltMC
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

package net.fabricmc.fabric.api.datafixer.v1;

import java.util.Optional;
import java.util.function.BiFunction;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.fabric.impl.datafixer.v1.QuiltDataFixesInternals;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Provides methods to register custom {@link DataFixer}s.
 */
public final class QuiltDataFixes {
	private QuiltDataFixes() {
		throw new RuntimeException("QuiltDataFixes only contains static declarations.");
	}

	/**
	 * A "base" version {@code 0} schema, for use by all mods.
	 * <p>
	 * This schema <em>must</em> be the first one added!
	 *
	 * @see DataFixerBuilder#addSchema(int, BiFunction)
	 */
	public static final BiFunction<Integer, Schema, Schema> BASE_SCHEMA = (version, parent) -> {
		checkArgument(version == 0, "version must be 0");
		checkArgument(parent == null, "parent must be null");
		return QuiltDataFixesInternals.get().createBaseSchema();
	};

	/**
	 * Registers a new data fixer.
	 *
	 * @param modId          the mod identifier
	 * @param currentVersion the current version of the mod's data
	 * @param dataFixer      the data fixer
	 */
	public static void registerFixer(@NotNull String modId,
			@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
			@NotNull DataFixer dataFixer) {
		requireNonNull(modId, "modId cannot be null");
		//noinspection ConstantConditions
		checkArgument(currentVersion >= 0, "currentVersion must be positive");
		requireNonNull(dataFixer, "dataFixer cannot be null");

		if (isFrozen()) {
			throw new IllegalStateException("Can't register data fixer after registry is frozen");
		}

		QuiltDataFixesInternals.get().registerFixer(modId, currentVersion, dataFixer);
	}

	/**
	 * Registers a new data fixer.
	 *
	 * @param mod            the mod container
	 * @param currentVersion the current version of the mod's data
	 * @param dataFixer      the data fixer
	 */
	public static void registerFixer(@NotNull ModContainer mod,
			@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
			@NotNull DataFixer dataFixer) {
		requireNonNull(mod, "mod cannot be null");

		registerFixer(mod.getMetadata().getId(), currentVersion, dataFixer);
	}

	/**
	 * Builds and registers a new data fixer.
	 *
	 * @param mod              the mod container
	 * @param dataFixerBuilder the data fixer builder
	 */
	public static void buildAndRegisterFixer(@NotNull ModContainer mod,
			@NotNull QuiltDataFixerBuilder dataFixerBuilder) {
		requireNonNull(mod, "mod cannot be null");
		requireNonNull(dataFixerBuilder, "data fixer builder cannot be null");

		registerFixer(mod.getMetadata().getId(), dataFixerBuilder.getDataVersion(),
				dataFixerBuilder.build(Util::getBootstrapExecutor));
	}

	/**
	 * Gets a mod's data fixer.
	 *
	 * @param modId the mod identifier
	 * @return the mod's data fixer, or empty if the mod hasn't registered one
	 */
	public static @NotNull Optional<DataFixer> getFixer(@NotNull String modId) {
		requireNonNull(modId, "modId cannot be null");

		QuiltDataFixesInternals.DataFixerEntry entry = QuiltDataFixesInternals.get().getFixerEntry(modId);
		if (entry == null) {
			return Optional.empty();
		}
		return Optional.of(entry.dataFixer());
	}

	/**
	 * Gets a mod's data version from a {@link NbtCompound}.
	 *
	 * @param compound the compound
	 * @param modId    the mod identifier
	 * @return the mod's data version, or {@code 0} if the compound has no data for that mod
	 */
	@Contract(pure = true)
	@Range(from = 0, to = Integer.MAX_VALUE)
	public static int getModDataVersion(@NotNull NbtCompound compound, @NotNull String modId) {
		requireNonNull(compound, "compound cannot be null");
		requireNonNull(modId, "modId cannot be null");

		return QuiltDataFixesInternals.getModDataVersion(compound, modId);
	}

	/**
	 * Checks if the data fixer registry is frozen.
	 *
	 * @return {@code true} if frozen, or {@code false} otherwise.
	 */
	@Contract(pure = true)
	public static boolean isFrozen() {
		return QuiltDataFixesInternals.get().isFrozen();
	}
}
