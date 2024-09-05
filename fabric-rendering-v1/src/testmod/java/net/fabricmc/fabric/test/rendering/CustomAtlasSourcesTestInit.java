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

package net.fabricmc.fabric.test.rendering;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class CustomAtlasSourcesTestInit implements ModInitializer {
	public static final RegistryKey<Item> DOUBLE_IRON_INGOT_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("fabric-rendering-v1-testmod", "double_iron_ingot"));
	public static final Item DOUBLE_IRON_INGOT = new Item(new Item.Settings().registryKey(DOUBLE_IRON_INGOT_KEY));

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, DOUBLE_IRON_INGOT_KEY, DOUBLE_IRON_INGOT);
	}
}
