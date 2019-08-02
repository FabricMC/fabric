/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabric.test;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.LootJsonParser;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Items;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.entry.LootEntry;

public class LootTableTestMod implements ModInitializer {
	private static final String LOOT_ENTRY_JSON = "{\"type\":\"minecraft:item\",\"name\":\"minecraft:apple\"}";

	@Override
	public void onInitialize() {
		LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
			if ("minecraft:blocks/dirt".equals(id.toString())) {
				LootEntry entryFromString = LootJsonParser.read(LOOT_ENTRY_JSON, LootEntry.class);

				LootPool pool = FabricLootPoolBuilder.builder()
						.withEntry(ItemEntry.builder(Items.FEATHER))
						.withEntry(entryFromString)
						.withRolls(ConstantLootTableRange.create(1))
						.withCondition(SurvivesExplosionLootCondition.builder())
						.build();



				supplier.withPool(pool);
			}
		});
	}
}
