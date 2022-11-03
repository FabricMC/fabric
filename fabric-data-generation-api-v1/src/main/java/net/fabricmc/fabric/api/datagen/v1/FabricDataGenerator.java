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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.util.registry.RegistryWrapper;

import net.fabricmc.loader.api.ModContainer;

/**
 * An extension to vanilla's {@link DataGenerator} providing mod specific data, and helper functions.
 */
public final class FabricDataGenerator extends DataGenerator {
	private final ModContainer modContainer;
	private final boolean strictValidation;
	private final FabricDataOutput fabricOutput;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

	@ApiStatus.Internal
	public FabricDataGenerator(Path output, ModContainer mod, boolean strictValidation, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, SharedConstants.getGameVersion(), true);
		this.modContainer = Objects.requireNonNull(mod);
		this.strictValidation = strictValidation;
		this.fabricOutput = new FabricDataOutput(mod, output, strictValidation);
		this.registriesFuture = registriesFuture;
	}

	public Pack createPack() {
		return new Pack(true, modContainer.getMetadata().getName(), this.fabricOutput);
	}

	public Pack createSubPack(String packName) {
		Path path = this.output.resolvePath(DataOutput.OutputType.DATA_PACK).resolve(getModId()).resolve("datapacks").resolve(packName);
		return new Pack(true, packName, new FabricDataOutput(modContainer, path, strictValidation));
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

	/**
	 * @deprecated Please use {@link FabricDataGenerator#createPack()}
	 */
	@Override
	@Deprecated
	public DataGenerator.Pack createVanillaPack(boolean shouldRun) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated Please use {@link FabricDataGenerator#createSubPack(String)}
	 */
	@Override
	@Deprecated
	public DataGenerator.Pack createVanillaSubPack(boolean shouldRun, String packName) {
		throw new UnsupportedOperationException();
	}

	public final class Pack extends DataGenerator.Pack {
		private Pack(boolean shouldRun, String name, FabricDataOutput output) {
			super(shouldRun, name, output);
		}

		/**
		 * Method to register a {@link Factory} to create a {@link DataProvider} that has a single argument constructor for a {@link FabricDataOutput}.
		 *
		 * @return The {@link DataProvider}
		 */
		public <T extends DataProvider> T addProvider(Factory<T> factory) {
			return super.addProvider(output -> factory.create((FabricDataOutput) output));
		}

		public <T extends DataProvider> T addProvider(RegistryDependentFactory<T> factory) {
			return super.addProvider(output -> factory.create((FabricDataOutput) output, registriesFuture));
		}

		@FunctionalInterface
		public interface Factory<T extends DataProvider> {
			T create(FabricDataOutput output);
		}

		@FunctionalInterface
		public interface RegistryDependentFactory<T extends DataProvider> {
			T create(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture);
		}
	}
}
