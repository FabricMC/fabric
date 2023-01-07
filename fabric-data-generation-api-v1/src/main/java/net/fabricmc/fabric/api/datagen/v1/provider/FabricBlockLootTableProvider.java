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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

/**
 * Extend this class and implement {@link FabricBlockLootTableProvider#generate}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class FabricBlockLootTableProvider extends BlockLootTableGenerator implements FabricLootTableProvider {
	private final FabricDataOutput output;
	private final Set<Identifier> excludedFromStrictValidation = new HashSet<>();

	protected FabricBlockLootTableProvider(FabricDataOutput dataOutput) {
		super(Collections.emptySet(), FeatureFlags.FEATURE_MANAGER.getFeatureSet());
		this.output = dataOutput;
	}

	/**
	 * Implement this method to add block drops.
	 *
	 * <p>Use the range of {@link BlockLootTableGenerator#addDrop} methods to generate block drops.
	 */
	@Override
	public abstract void generate();

	/**
	 * Disable strict validation for the passed block.
	 */
	public void excludeFromStrictValidation(Block block) {
		excludedFromStrictValidation.add(Registries.BLOCK.getId(block));
	}

	@Override
	public LootContextType getLootContextType() {
		return LootContextTypes.BLOCK;
	}

	@Override
	public FabricDataOutput getFabricDataOutput() {
		return output;
	}

	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		generate();

		for (Map.Entry<Identifier, LootTable.Builder> entry : lootTables.entrySet()) {
			Identifier identifier = entry.getKey();

			if (identifier.equals(LootTables.EMPTY)) {
				continue;
			}

			biConsumer.accept(identifier, entry.getValue());
		}

		if (output.isStrictValidationEnabled()) {
			Set<Identifier> missing = Sets.newHashSet();

			for (Identifier blockId : Registries.BLOCK.getIds()) {
				if (blockId.getNamespace().equals(output.getModId())) {
					Identifier blockLootTableId = Registries.BLOCK.get(blockId).getLootTableId();

					if (blockLootTableId.getNamespace().equals(output.getModId())) {
						if (!lootTables.containsKey(blockLootTableId)) {
							missing.add(blockId);
						}
					}
				}
			}

			missing.removeAll(excludedFromStrictValidation);

			if (!missing.isEmpty()) {
				throw new IllegalStateException("Missing loot table(s) for %s".formatted(missing));
			}
		}
	}

	@Override
	public String getName() {
		return "Block Loot Tables";
	}
}
