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
import java.util.concurrent.CompletableFuture;

import net.minecraft.data.DataWriter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.impl.datagen.loot.FabricLootTableProviderImpl;

/**
 * Extend this class and implement {@link #accept}. Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class SimpleFabricLootTableProvider implements FabricLootTableProvider {
	protected final FabricDataOutput output;
	protected final LootContextType lootContextType;

	public SimpleFabricLootTableProvider(FabricDataOutput output, LootContextType lootContextType) {
		this.output = output;
		this.lootContextType = lootContextType;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return FabricLootTableProviderImpl.run(writer, this, lootContextType, output);
	}

	@Override
	public String getName() {
		return Objects.requireNonNull(LootContextTypes.getId(lootContextType), "Could not get id for loot context type") + " Loot Table";
	}
}
