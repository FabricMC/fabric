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

package net.fabricmc.fabric.loot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.loot.LootTableLoadingCallback;
import net.minecraft.item.Items;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.world.loot.entry.ItemEntry;

public class LootTableMod implements ModInitializer {
	@Override
	public void onInitialize() {
		LootTableLoadingCallback.EVENT.register((manager, id, supplier) -> {
			if ("minecraft:blocks/dirt".equals(id.toString())) {
				LootPool pool = LootPool.create()
						.withEntry(ItemEntry.builder(Items.FEATHER))
						.withRolls(ConstantLootTableRange.create(1))
						.withCondition(SurvivesExplosionLootCondition.method_871())
						.build();

				supplier.addPools(pool);
			}
		});
	}
}
