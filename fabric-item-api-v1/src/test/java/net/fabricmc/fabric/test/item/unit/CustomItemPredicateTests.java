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

package net.fabricmc.fabric.test.item.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;
import net.fabricmc.fabric.impl.item.FabricItemPredicateCodec;
import net.fabricmc.fabric.test.item.CustomItemPredicateTest;
import net.fabricmc.fabric.test.item.CustomItemPredicateTest.NoOpItemPredicate;
import net.fabricmc.fabric.test.item.CustomItemPredicateTest.TierItemPredicate;
import net.fabricmc.fabric.test.item.CustomItemPredicateTest.TieredItem;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CustomItemPredicateTests {
	@BeforeAll
	static void setup() {
		SharedConstants.createGameVersion();
		Bootstrap.initialize();

		new CustomItemPredicateTest().onInitialize();
	}

	@Test
	void custom_codec_results_same_serialized_form_as_vanilla() {
		NbtCompound nbt = new NbtCompound();
		nbt.putInt("someInt", 23);

		ItemPredicate predicate = ItemPredicate.Builder.create()
				.tag(TagKey.of(RegistryKeys.ITEM, new Identifier("something")))
				.items(Items.DIAMOND, Blocks.DIAMOND_BLOCK)
				.count(NumberRange.IntRange.between(3, 5))
				.durability(NumberRange.IntRange.between(2, 9))
				.enchantment(new EnchantmentPredicate(Enchantments.EFFICIENCY, NumberRange.IntRange.between(1, 3)))
				.storedEnchantment(new EnchantmentPredicate(Enchantments.LOOTING, NumberRange.IntRange.between(2, 4)))
				.potion(Potions.HEALING)
				.nbt(nbt)
				.build();

		DataResult<JsonElement> vanillaResult = FabricItemPredicateCodec.vanillaCodec.encodeStart(JsonOps.INSTANCE, predicate);
		assertTrue(vanillaResult.result().isPresent());
		JsonElement vanilla = vanillaResult.result().orElseThrow();
		String vanillaStr = JsonHelper.toSortedString(vanilla);

		DataResult<JsonElement> customResult = FabricItemPredicateCodec.customCodec.encodeStart(JsonOps.INSTANCE, predicate);
		assertTrue(customResult.result().isPresent());
		JsonElement custom = customResult.result().orElseThrow();
		String customStr = JsonHelper.toSortedString(custom);

		assertEquals(vanillaStr, customStr);
	}

	@Test
	void custom_predicate_serialization_and_deserialization() {
		ItemPredicate predicate = ItemPredicate.Builder.create()
				.custom(new TierItemPredicate(3))
				.custom(NoOpItemPredicate.INSTANCE_1)
				.custom(NoOpItemPredicate.INSTANCE_2)
				.build();

		DataResult<JsonElement> jsonResult = ItemPredicate.CODEC.encodeStart(JsonOps.INSTANCE, predicate);
		assertTrue(jsonResult.result().isPresent());
		JsonElement json = jsonResult.result().orElseThrow();

		DataResult<ItemPredicate> reserializedResult = ItemPredicate.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);
		assertTrue(reserializedResult.result().isPresent());
		ItemPredicate reserialized = reserializedResult.result().orElseThrow();

		assertTrue(reserialized.tag().isEmpty());
		assertTrue(reserialized.items().isEmpty());
		assertEquals(NumberRange.IntRange.ANY, reserialized.count());
		assertEquals(NumberRange.IntRange.ANY, reserialized.durability());
		assertTrue(reserialized.enchantments().isEmpty());
		assertTrue(reserialized.storedEnchantments().isEmpty());
		assertTrue(reserialized.potion().isEmpty());
		assertTrue(reserialized.nbt().isEmpty());

		List<CustomItemPredicate> custom = reserialized.custom();
		assertEquals(3, custom.size());
		assertInstanceOf(TierItemPredicate.class, custom.get(0));
		assertEquals(3, ((TierItemPredicate) custom.get(0)).tier());
		assertSame(NoOpItemPredicate.INSTANCE_1, custom.get(1));
		assertSame(NoOpItemPredicate.INSTANCE_2, custom.get(2));
	}

	@Test
	void custom_codec_must_fail_on_unknown_key() {
		// language=json
		String input = """
				{
				  "items": [
				    "minecraft:enchanted_golden_apple"
				  ],
				  "nbt": "{display:{Name:\\"Example\\"}}",
				  "some:unknown_predicate": true
				}
				""";

		JsonObject json = JsonHelper.deserialize(input);
		DataResult<Pair<ItemPredicate, JsonElement>> result = ItemPredicate.CODEC.decode(JsonOps.INSTANCE, json);

		assertTrue(result.error().isPresent());
		assertEquals("Unknown custom predicate id some:unknown_predicate", result.error().orElseThrow().message());
	}

	@Test
	void match_stack() {
		ItemStack tier3count1 = new ItemStack(new TieredItem(3), 1);
		ItemStack tier3count2 = new ItemStack(new TieredItem(3), 2);
		ItemStack tier4 = new ItemStack(new TieredItem(4), 1);

		ItemPredicate predicate = ItemPredicate.Builder.create()
				.custom(new TierItemPredicate(3))
				.count(NumberRange.IntRange.exactly(1))
				.build();

		assertTrue(predicate.test(tier3count1));
		assertFalse(predicate.test(tier3count2));
		assertFalse(predicate.test(tier4));
	}
}
