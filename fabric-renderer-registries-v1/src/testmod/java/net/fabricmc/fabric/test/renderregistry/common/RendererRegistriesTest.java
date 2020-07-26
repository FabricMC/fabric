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

package net.fabricmc.fabric.test.renderregistry.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.test.renderregistry.common.cooldown.CooldownItem;
import net.fabricmc.fabric.test.renderregistry.common.durabilitybar.StorageItem;
import net.fabricmc.fabric.test.renderregistry.common.durabilitybar.WaterLavaBucket;

public class RendererRegistriesTest implements ModInitializer {
	public static final String ID = "fabric-renderer-registries-v1-testmod";

	// Count Label related items
	public static final Item OBFUSCATED_COUNT = new StorageItem(new Item.Settings().group(ItemGroup.MISC));

	// Durability Bar related items
	public static final Item ENERGY_STORAGE = new StorageItem(new Item.Settings().group(ItemGroup.MISC));
	public static final Item MANA_STORAGE = new StorageItem(new Item.Settings().group(ItemGroup.MISC));
	public static final Item WATER_LAVA_BUCKET = new WaterLavaBucket(new Item.Settings().group(ItemGroup.MISC));
	public static final Item DISCO_BALL = new Item(new Item.Settings().group(ItemGroup.MISC));

	// Custom Cooldown Overlay items
	public static final Item LONG_COOLDOWN = new CooldownItem(new Item.Settings().group(ItemGroup.MISC), 4 * 20);
	public static final Item HIDDEN_COOLDOWN = new CooldownItem(new Item.Settings().group(ItemGroup.MISC), 4 * 20);

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}

	@Override
	public void onInitialize() {
		// Items used to demonstrate custom count labels
		Registry.register(Registry.ITEM, id("obfuscated_count"), OBFUSCATED_COUNT);

		// Items used to demonstrate custom durability bars
		Registry.register(Registry.ITEM, id("energy_storage"), ENERGY_STORAGE);
		Registry.register(Registry.ITEM, id("mana_storage"), MANA_STORAGE);
		Registry.register(Registry.ITEM, id("water_lava_bucket"), WATER_LAVA_BUCKET);
		Registry.register(Registry.ITEM, id("disco_ball"), DISCO_BALL);

		// Items used to demonstrate custom cooldown overlays
		Registry.register(Registry.ITEM, id("long_cooldown"), LONG_COOLDOWN);
		Registry.register(Registry.ITEM, id("hidden_cooldown"), HIDDEN_COOLDOWN);
	}
}
