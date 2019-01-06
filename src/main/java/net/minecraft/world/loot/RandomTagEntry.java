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

package net.minecraft.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.loot.FabricLootEntries;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.entry.LeafEntry;
import net.minecraft.world.loot.entry.TagEntry;
import net.minecraft.world.loot.function.LootFunction;

import java.util.function.Consumer;

/**
 * A loot table entry that selects a random item from a tag.
 *
 * Can be used with the identifier "fabric:random_from_tag".
 */
public class RandomTagEntry extends LeafEntry {
	private final TagEntry delegate;

	public RandomTagEntry(TagEntry delegate, int int_1, int int_2, LootCondition[] lootConditions_1, LootFunction[] lootFunctions_1) {
		super(int_1, int_2, lootConditions_1, lootFunctions_1);
		this.delegate = delegate;
	}

	@Override
	protected void drop(Consumer<ItemStack> var1, LootContext var2) {
		var1.accept(new ItemStack(((FabricLootEntries.TagEntryDelegate) delegate).fabric_getTag().getRandom(var2.getRandom())));
	}

	@Override
	public LootChoiceProvider and(LootChoiceProvider lootChoiceProvider_1) {
		return delegate.and(lootChoiceProvider_1);
	}

	@Override
	public LootChoiceProvider or(LootChoiceProvider lootChoiceProvider_1) {
		return delegate.or(lootChoiceProvider_1);
	}

	// Delegates the serialization to TagEntry.Serializer.
	public static class Serializer extends LeafEntry.Serializer<RandomTagEntry> {
		private final TagEntry.Serializer serializer = new TagEntry.Serializer();

		public Serializer(Identifier id) {
			super(id, RandomTagEntry.class);
		}

		@Override
		protected RandomTagEntry method_443(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootCondition[] var5, LootFunction[] var6) {
			if (!var1.has("expand")) {
				var1.addProperty("expand", false);
			}

			return new RandomTagEntry(serializer.fromJson(var1, var2, var5), var3, var4, var5, var6);
		}
	}
}
