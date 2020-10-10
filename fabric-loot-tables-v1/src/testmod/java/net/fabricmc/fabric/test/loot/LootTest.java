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

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.LootEntryTypeRegistry;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;

public class LootTest implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Gson LOOT_GSON = LootGsons.getTableGsonBuilder().create();
	private static final String LOOT_ENTRY_JSON = "{\"type\":\"minecraft:item\",\"name\":\"minecraft:apple\"}";

	@Override
	public void onInitialize() {
		// Test loot entry
		LootEntryTypeRegistry.INSTANCE.register(new Identifier("fabric", "extended_tag"), new TestSerializer());

		// Test loot table load event
		LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
			if ("minecraft:blocks/dirt".equals(id.toString())) {
				LootPoolEntry entryFromString = LOOT_GSON.fromJson(LOOT_ENTRY_JSON, LootPoolEntry.class);

				LootPool pool = FabricLootPoolBuilder.builder()
						.withEntry(ItemEntry.builder(Items.FEATHER).build())
						.withEntry(entryFromString)
						.rolls(ConstantLootTableRange.create(1))
						.withCondition(SurvivesExplosionLootCondition.builder().build())
						.build();

				supplier.withPool(pool);
			}
		});
	}

	private static class TestSerializer extends LootPoolEntry.Serializer<TagEntry> {
		private static final TagEntry.Serializer SERIALIZER = new TagEntry.Serializer();

		@Override
		public void addEntryFields(JsonObject json, TagEntry entry, JsonSerializationContext context) {
			SERIALIZER.addEntryFields(json, entry, context);
			json.addProperty("fabric", true);
		}

		@Override
		public TagEntry fromJson(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
			LOGGER.info("Is this a Fabric loot entry? " + JsonHelper.getBoolean(var1, "fabric", true));
			return SERIALIZER.fromJson(var1, var2, var3);
		}
	}
}
