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

package net.fabricmc.fabric.api.datagen.v1;

import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import net.fabricmc.loader.api.ModContainer;

/**
 * An extension to vanilla's {@link DataGenerator} providing mod specific data, and helper functions.
 */
public final class FabricDataGenerator extends DataGenerator {
	private final ModContainer modContainer;
	private final boolean strictValidation;

	@ApiStatus.Internal
	public FabricDataGenerator(Path output, ModContainer mod, boolean strictValidation) {
		super(output, Collections.emptyList());
		this.modContainer = mod;
		this.strictValidation = strictValidation;
	}

	/**
	 * Helper overloaded method to aid with registering a {@link DataProvider} that has a single argument constructor for a {@link FabricDataGenerator}.
	 *
	 * @return The {@link DataProvider}
	 */
	public <P extends DataProvider> P addProvider(Function<FabricDataGenerator, P> provider) {
		P p = provider.apply(this);
		addProvider(p);
		return p;
	}

	/**
	 * Returns the {@link ModContainer} for the mod that this data generator has been created for.
	 *
	 * @return a {@link ModContainer} instance
	 */
	public ModContainer getModContainer() {
		return modContainer;
	}

	/**
	 * Returns the mod ID for the mod that this data generator has been created for.
	 *
	 * @return a mod ID
	 */
	public String getModId() {
		return getModContainer().getMetadata().getId();
	}

	/**
	 * When enabled data providers can do strict validation to ensure that all entries have data generated for them.
	 *
	 * @return if strict validation should be enabled
	 */
	public boolean isStrictValidationEnabled() {
		return strictValidation;
	}
}
