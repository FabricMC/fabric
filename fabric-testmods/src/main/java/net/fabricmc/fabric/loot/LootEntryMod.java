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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.LootEntryTypeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.entry.TagEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootEntryMod implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		LootEntryTypeRegistry.INSTANCE.register(new TestSerializer());
	}

	private static class TestSerializer extends LootEntry.Serializer<TagEntry> {
		private static final TagEntry.Serializer SERIALIZER = new TagEntry.Serializer();

		public TestSerializer() {
			super(new Identifier("fabric", "extended_tag"), TagEntry.class);
		}

		@Override
		public void toJson(JsonObject obj, TagEntry entry, JsonSerializationContext context) {
			SERIALIZER.method_451(obj, entry, context);
			obj.addProperty("fabric", true);
		}

		@Override
		public TagEntry fromJson(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
			LOGGER.info("Is this a Fabric loot entry? " + JsonHelper.getBoolean(var1, "fabric", true));
			return SERIALIZER.fromJson(var1, var2, var3);
		}
	}
}

