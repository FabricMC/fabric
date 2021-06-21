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

package net.fabricmc.fabric.test.loot;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableLoadingCallback;

public class LootTest implements ModInitializer {
	@Override
	public void onInitialize() {
		// Test loot table load event
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, tableBuilder, setter) -> {
			if (Blocks.WHITE_WOOL.getLootTableId().equals(id)) {
				// Add gold ingot to white wool drops
				LootPool pool = FabricLootPoolBuilder.create()
						.with(ItemEntry.builder(Items.GOLD_INGOT).build())
						.conditionally(SurvivesExplosionLootCondition.builder().build())
						.build();

				tableBuilder.pool(pool);
			} else if (Blocks.BLACK_WOOL.getLootTableId().equals(id)) {
				// Replace black wool drops with an iron ingot
				FabricLootPoolBuilder pool = FabricLootPoolBuilder.create()
						.with(ItemEntry.builder(Items.IRON_INGOT));

				setter.set(FabricLootTableBuilder.create().pool(pool).build());
			} else if (EntityType.PIG.getLootTableId().equals(id)) {
				// Add diamonds with bonus rolls to the pig loot table (bonus rolls don't work for blocks)
				LootPool pool = FabricLootPoolBuilder.create()
						.bonusRolls(new UniformLootTableRange(5f))
						.with(ItemEntry.builder(Items.DIAMOND))
						.build();

				tableBuilder.pool(pool);
			}
		});

		// Test that the event is stopped when the loot table is replaced
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, tableBuilder, setter) -> {
			if (Blocks.BLACK_WOOL.getLootTableId().equals(id)) {
				throw new AssertionError("Event should have been stopped from replaced loot table");
			}
		});
	}
}
