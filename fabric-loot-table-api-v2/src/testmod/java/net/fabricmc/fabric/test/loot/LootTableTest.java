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
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.event.LootTableLoadingCallback;

public final class LootTableTest implements ModInitializer {
	@Override
	public void onInitialize() {
		Identifier sandTableId = Blocks.SAND.getLootTableId();
		Identifier dirtTableId = Blocks.DIRT.getLootTableId();

		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) -> {
			if (id.equals(sandTableId)) {
				// Add a beetroot to sand drops
				table.pool(LootPool.builder().with(ItemEntry.builder(Items.BEETROOT)));
			} else if (id.equals(dirtTableId)) {
				// Replace the dirt loot table with one that drops gravel instead
				setter.set(LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(Items.GRAVEL))).build());
			}
		});
	}
}
