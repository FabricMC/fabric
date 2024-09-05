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

package net.fabricmc.fabric.test.datagen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

public class DataGeneratorTestContent implements ModInitializer {
	public static final String MOD_ID = "fabric-data-gen-api-v1-testmod";

	public static Block SIMPLE_BLOCK;
	public static Block BLOCK_WITHOUT_ITEM;
	public static Block BLOCK_WITHOUT_LOOT_TABLE;
	public static Block BLOCK_WITH_VANILLA_LOOT_TABLE;
	public static Block BLOCK_THAT_DROPS_NOTHING;

	public static final RegistryKey<ItemGroup> SIMPLE_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "simple"));

	public static final RegistryKey<Registry<TestDatagenObject>> TEST_DATAGEN_DYNAMIC_REGISTRY_KEY =
			RegistryKey.ofRegistry(Identifier.of("fabric", "test_datagen_dynamic"));
	public static final RegistryKey<TestDatagenObject> TEST_DYNAMIC_REGISTRY_ITEM_KEY = RegistryKey.of(
			TEST_DATAGEN_DYNAMIC_REGISTRY_KEY,
			Identifier.of(MOD_ID, "tiny_potato")
	);
	public static final RegistryKey<TestDatagenObject> TEST_DYNAMIC_REGISTRY_EXTRA_ITEM_KEY = RegistryKey.of(
			TEST_DATAGEN_DYNAMIC_REGISTRY_KEY,
			Identifier.of(MOD_ID, "tinier_potato")
	);
	// Empty registry
	public static final RegistryKey<Registry<TestDatagenObject>> TEST_DATAGEN_DYNAMIC_EMPTY_REGISTRY_KEY =
			RegistryKey.ofRegistry(Identifier.of("fabric", "test_datagen_dynamic_empty"));

	@Override
	public void onInitialize() {
		SIMPLE_BLOCK = createBlock("simple_block", true, AbstractBlock.Settings.create());
		BLOCK_WITHOUT_ITEM = createBlock("block_without_item", false, AbstractBlock.Settings.create());
		BLOCK_WITHOUT_LOOT_TABLE = createBlock("block_without_loot_table", false, AbstractBlock.Settings.create());
		BLOCK_WITH_VANILLA_LOOT_TABLE = createBlock("block_with_vanilla_loot_table", false, AbstractBlock.Settings.create().lootTable(Blocks.STONE.getLootTableKey()));
		BLOCK_THAT_DROPS_NOTHING = createBlock("block_that_drops_nothing", false, AbstractBlock.Settings.create().dropsNothing());

		ItemGroupEvents.modifyEntriesEvent(SIMPLE_ITEM_GROUP).register(entries -> entries.add(SIMPLE_BLOCK));

		Registry.register(Registries.ITEM_GROUP, SIMPLE_ITEM_GROUP, FabricItemGroup.builder()
				.icon(() -> new ItemStack(Items.DIAMOND_PICKAXE))
				.displayName(Text.translatable("fabric-data-gen-api-v1-testmod.simple_item_group"))
				.build());

		DynamicRegistries.register(TEST_DATAGEN_DYNAMIC_REGISTRY_KEY, TestDatagenObject.CODEC);
		DynamicRegistries.register(TEST_DATAGEN_DYNAMIC_EMPTY_REGISTRY_KEY, TestDatagenObject.CODEC);
	}

	private static Block createBlock(String name, boolean hasItem, AbstractBlock.Settings settings) {
		Identifier identifier = Identifier.of(MOD_ID, name);
		Block block = Registry.register(Registries.BLOCK, identifier, new Block(settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, identifier))));

		if (hasItem) {
			Registry.register(Registries.ITEM, identifier, new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, identifier))));
		}

		return block;
	}

	public record TestDatagenObject(String value) {
		public static final Codec<TestDatagenObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("value").forGetter(TestDatagenObject::value)
		).apply(instance, TestDatagenObject::new));
	}
}
