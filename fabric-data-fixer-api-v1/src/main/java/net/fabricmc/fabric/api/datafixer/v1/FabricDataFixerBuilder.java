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
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.DataFixerUpper;

import net.minecraft.util.Util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;

import net.fabricmc.fabric.impl.datafixer.v1.FabricDataFixesInternals;
import net.fabricmc.loader.api.ModContainer;

/**
 * An extended variant of the {@link DataFixerBuilder} class, which provides an extra method.
 */
public class FabricDataFixerBuilder extends DataFixerBuilder {
	// From QSL.
	protected final int dataVersion;

	/**
	 * Creates a new {@code FabricDataFixerBuilder}.
	 *
	 * @param dataVersion the current data version
	 */
	public FabricDataFixerBuilder(@Range(from = 0, to = Integer.MAX_VALUE) int dataVersion) {
		super(dataVersion);
		this.dataVersion = dataVersion;
	}

	/**
	 * Creates a new {@code FabricDataFixerBuilder}. This method gets the current version from
	 * the {@code fabric-data-fixer-api-v1:version} field in the {@code custom} object of
	 * the {@code fabric.mod.json} file of {@code mod}. To specify the version
	 * manually, use the other overload.
	 *
	 * @param mod the mod container
	 * @return the data fixer builder
	 * @throws RuntimeException if the version field does not exist or is not a number
	 */
	public static FabricDataFixerBuilder create(ModContainer mod) {
		Objects.requireNonNull(mod, "mod cannot be null");
		int dataVersion = FabricDataFixesInternals.getDataVersionFromMetadata(mod);
		return new FabricDataFixerBuilder(dataVersion);
	}

	/**
	 * @return the current data version
	 */
	@Range(from = 0, to = Integer.MAX_VALUE)
	public int getDataVersion() {
		return this.dataVersion;
	}

	/**
	 * Builds the final {@code DataFixer}.
	 *
	 * <p>This will build either an {@linkplain #build() unoptimized fixer} or an
	 * optimized fixer, depending on the vanilla game's settings.
	 *
	 * @param types          the set of required {@link com.mojang.datafixers.DSL.TypeReference}s, only used if the game is using optimized data fixers
	 * @param executorGetter the executor supplier, only invoked if the game is using optimized data fixers
	 * @return the newly built data fixer
	 */
	@Contract(value = "_, _ -> new")
	public DataFixerUpper build(Set<DSL.TypeReference> types, Supplier<Executor> executorGetter) {
		Objects.requireNonNull(executorGetter, "executorGetter cannot be null");
		return (DataFixerUpper) (types.isEmpty() ? this.build().fixer() : Util.make(() -> {
			Result result = this.build();
			result.optimize(types, executorGetter.get()).join();
			return result.fixer();
		}));
	}
}
