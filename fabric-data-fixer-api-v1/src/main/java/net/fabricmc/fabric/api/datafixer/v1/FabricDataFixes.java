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

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;

import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.impl.datafixer.v1.FabricDataFixesInternals;
import net.fabricmc.loader.api.ModContainer;

/**
 * Provides methods to register custom {@link DataFixer}s.
 */
public final class FabricDataFixes {
	private FabricDataFixes() {
		throw new RuntimeException("FabricDataFixes only contains static declarations.");
	}

	/**
	 * A "base" version {@code 0} schema, for use by all mods.
	 *
	 * <p>This schema <em>must</em> be the first one added!
	 *
	 * @see DataFixerBuilder#addSchema(int, BiFunction)
	 */
	public static final BiFunction<Integer, Schema, Schema> BASE_SCHEMA = (version, parent) -> {
		Preconditions.checkArgument(version == 0, "version must be 0");
		Preconditions.checkArgument(parent == null, "parent must be null");
		return FabricDataFixesInternals.get().createBaseSchema();
	};

	/**
	 * Registers a new data fixer.
	 *
	 * @param modId          the mod ID
	 * @param currentVersion the current version of the mod's data
	 * @param dataFixer      the data fixer
	 * @throws IllegalArgumentException if the data fixer for {@code modId} is already registered
	 */
	public static void registerFixer(String modId,
			@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
			DataFixer dataFixer) {
		Objects.requireNonNull(modId, "modId cannot be null");
		//noinspection ConstantConditions
		Preconditions.checkArgument(currentVersion >= 0, "currentVersion must be positive");
		Objects.requireNonNull(dataFixer, "dataFixer cannot be null");

		if (isFrozen()) {
			throw new IllegalStateException("Can't register data fixer after registry is frozen");
		}

		FabricDataFixesInternals.get().registerFixer(modId, currentVersion, dataFixer);
	}

	/**
	 * Registers a new data fixer.
	 *
	 * @param mod            the mod container
	 * @param currentVersion the current version of the mod's data
	 * @param dataFixer      the data fixer
	 * @throws IllegalArgumentException if the data fixer for {@code mod} is already registered
	 */
	public static void registerFixer(ModContainer mod,
			@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
			DataFixer dataFixer) {
		Objects.requireNonNull(mod, "mod cannot be null");

		registerFixer(mod.getMetadata().getId(), currentVersion, dataFixer);
	}

	/**
	 * Registers a new data fixer. This method gets the current version from
	 * the {@code fabric-data-fixer-api-v1:version} field in the {@code custom} object of
	 * the {@code fabric.mod.json} file of {@code mod}. To specify the version
	 * manually, use the other overloads.
	 *
	 * @param mod            the mod container
	 * @param dataFixer      the data fixer
	 * @throws RuntimeException if the version field does not exist or is not a number
	 * @throws IllegalArgumentException if the data fixer for {@code mod} is already registered
	 */
	public static void registerFixer(ModContainer mod, DataFixer dataFixer) {
		Objects.requireNonNull(mod, "mod cannot be null");
		registerFixer(mod.getMetadata().getId(), FabricDataFixesInternals.getDataVersionFromMetadata(mod), dataFixer);
	}

	/**
	 * Builds and registers a new data fixer.
	 *
	 * @param mod              the mod container
	 * @param builder the data fixer builder
	 * @throws IllegalArgumentException if the data fixer for {@code mod} is already registered
	 */
	public static void buildAndRegisterFixer(ModContainer mod,
			FabricDataFixerBuilder builder) {
		Objects.requireNonNull(mod, "mod cannot be null");
		Objects.requireNonNull(builder, "data fixer builder cannot be null");

		registerFixer(mod.getMetadata().getId(), builder.getDataVersion(),
				builder.build());
	}

	/**
	 * Gets a mod's data fixer.
	 *
	 * @param modId the mod ID
	 * @return the mod's data fixer, or empty optional if the mod hasn't registered one
	 */
	public static Optional<DataFixer> getFixer(String modId) {
		Objects.requireNonNull(modId, "modId cannot be null");

		FabricDataFixesInternals.DataFixerEntry entry = FabricDataFixesInternals.get().getFixerEntry(modId);

		if (entry == null) {
			return Optional.empty();
		}

		return Optional.of(entry.dataFixer());
	}

	/**
	 * Gets a mod's data version from a {@link NbtCompound}.
	 *
	 * @param nbt the NBT compound
	 * @param modId    the mod ID
	 * @return the mod's data version, or {@code 0} if the NBT has no data for that mod
	 */
	@Contract(pure = true)
	@Range(from = 0, to = Integer.MAX_VALUE)
	public static int getModDataVersion(NbtCompound nbt, String modId) {
		Objects.requireNonNull(nbt, "compound cannot be null");
		Objects.requireNonNull(modId, "modId cannot be null");

		return FabricDataFixesInternals.getModDataVersion(nbt, modId);
	}

	/**
	 * Checks if the data fixer registry is frozen. Data fixers cannot be registered
	 * after the registry gets frozen.
	 *
	 * @return {@code true} if frozen, or {@code false} otherwise.
	 */
	@Contract(pure = true)
	public static boolean isFrozen() {
		return FabricDataFixesInternals.get().isFrozen();
	}
}
