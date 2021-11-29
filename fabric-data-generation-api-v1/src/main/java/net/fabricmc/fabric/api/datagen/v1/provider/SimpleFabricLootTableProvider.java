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

package net.fabricmc.fabric.api.datagen.v1.provider;

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Extend this class and implement {@link java.util.function.Consumer#accept}. Register an instance of the class with {@link FabricDataGenerator#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
 */
public abstract class SimpleFabricLootTableProvider implements FabricLootTableProvider {
	protected final FabricDataGenerator dataGenerator;
	protected final LootContextType lootContextType;

	public SimpleFabricLootTableProvider(FabricDataGenerator dataGenerator, LootContextType lootContextType) {
		this.dataGenerator = dataGenerator;
		this.lootContextType = lootContextType;
	}

	@ApiStatus.Internal
	@Override
	public final LootContextType getLootContextType() {
		return lootContextType;
	}

	@ApiStatus.Internal
	@Override
	public final FabricDataGenerator getFabricDataGenerator() {
		return dataGenerator;
	}

	@Override
	public String getName() {
		return Objects.requireNonNull(LootContextTypes.getId(lootContextType), "Could not get id for loot context type") + " Loot Table";
	}
}
