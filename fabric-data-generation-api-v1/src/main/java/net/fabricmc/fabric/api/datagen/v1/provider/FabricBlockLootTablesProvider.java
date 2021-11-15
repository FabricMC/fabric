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

import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public abstract class FabricBlockLootTablesProvider extends BlockLootTableGenerator implements FabricLootTableProvider {
	private final FabricDataGenerator dataGenerator;

	protected FabricBlockLootTablesProvider(FabricDataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	protected abstract void generateBlockLootTables();

	@Override
	public LootContextType getLootContextType() {
		return LootContextTypes.BLOCK;
	}

	@Override
	public FabricDataGenerator getFabricDataGenerator() {
		return dataGenerator;
	}

	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		generateBlockLootTables();
		// TODO add a strict mode that ensures that all blocks for a mod have a loot table?

		for (Map.Entry<Identifier, LootTable.Builder> entry : lootTables.entrySet()) {
			Identifier identifier = entry.getKey();

			if (identifier.equals(LootTables.EMPTY)) {
				continue;
			}

			biConsumer.accept(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public String getName() {
		return "Block Loot Tables";
	}
}
