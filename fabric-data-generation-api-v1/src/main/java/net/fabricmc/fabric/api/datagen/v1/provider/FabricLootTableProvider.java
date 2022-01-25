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

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;

/**
 * A base interface for Loot table providers. You should not implement this class directly.
 *
 * <p>{@link FabricBlockLootTablesProvider} provides additional features specific to block drop loot tables.
 *
 * <p>Use {@link SimpleFabricLootTableProvider} for a simple abstract class that you can implement to handle standard loot table functions.
 */
@ApiStatus.NonExtendable
public interface FabricLootTableProvider extends Consumer<BiConsumer<Identifier, LootTable.Builder>>, DataProvider {
	Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	LootContextType getLootContextType();

	FabricDataGenerator getFabricDataGenerator();

	/**
	 * Return a new exporter that applies the specified conditions to any loot table it receives.
	 */
	default BiConsumer<Identifier, LootTable.Builder> withConditions(BiConsumer<Identifier, LootTable.Builder> exporter, ConditionJsonProvider... conditions) {
		Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
		return (id, table) -> {
			FabricDataGenHelper.addConditions(table, conditions);
			exporter.accept(id, table);
		};
	}

	@ApiStatus.Internal
	@Override
	default void run(DataCache cache) throws IOException {
		HashMap<Identifier, LootTable> builders = Maps.newHashMap();
		HashMap<Identifier, ConditionJsonProvider[]> conditionMap = new HashMap<>();

		accept((identifier, builder) -> {
			ConditionJsonProvider[] conditions = FabricDataGenHelper.consumeConditions(builder);
			conditionMap.put(identifier, conditions);

			if (builders.put(identifier, builder.type(getLootContextType()).build()) != null) {
				throw new IllegalStateException("Duplicate loot table " + identifier);
			}
		});

		for (Map.Entry<Identifier, LootTable> entry : builders.entrySet()) {
			JsonObject tableJson = (JsonObject) LootManager.toJson(entry.getValue());
			ConditionJsonProvider.write(tableJson, conditionMap.remove(entry.getKey()));

			DataProvider.writeToPath(GSON, cache, tableJson, getOutputPath(entry.getKey()));
		}
	}

	private Path getOutputPath(Identifier lootTableId) {
		return getFabricDataGenerator().getOutput().resolve("data/%s/loot_tables/%s.json".formatted(lootTableId.getNamespace(), lootTableId.getPath()));
	}
}
