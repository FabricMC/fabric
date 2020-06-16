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

package net.fabricmc.fabric.test.registry.sync;

import org.apache.commons.lang3.Validate;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public class RegistrySyncTest implements ModInitializer {
	/**
	 * These are system property's as it allows for easier testing with different run configurations.
	 */
	public static final boolean REGISTER_BLOCKS = Boolean.parseBoolean(System.getProperty("fabric.registry.sync.test.register.blocks", "true"));
	public static final boolean REGISTER_ITEMS = Boolean.parseBoolean(System.getProperty("fabric.registry.sync.test.register.items", "true"));

	@Override
	public void onInitialize() {
		if (REGISTER_BLOCKS) {
			for (int i = 0; i < 5; i++) {
				Block block = new Block(AbstractBlock.Settings.of(Material.STONE));
				Registry.register(Registry.BLOCK, new Identifier("registry_sync", "block_" + i), block);

				if (REGISTER_ITEMS) {
					BlockItem blockItem = new BlockItem(block, new Item.Settings());
					Registry.register(Registry.ITEM, new Identifier("registry_sync", "block_" + i), blockItem);
				}
			}

			Validate.isTrue(RegistryAttributeHolder.get(Registry.BLOCK).hasAttribute(RegistryAttribute.MODDED), "Modded block was registered but registry not marked as modded");

			if (REGISTER_ITEMS) {
				Validate.isTrue(RegistryAttributeHolder.get(Registry.ITEM).hasAttribute(RegistryAttribute.MODDED), "Modded item was registered but registry not marked as modded");
			}
		}

		SimpleRegistry<String> fabricRegistry = FabricRegistryBuilder.createSimple(String.class, new Identifier("registry_sync", "fabric_registry"))
				.attribute(RegistryAttribute.SYNCED)
				.buildAndRegister();

		Registry.register(fabricRegistry, new Identifier("registry_sync", "test"), "test");

		Validate.isTrue(Registry.REGISTRIES.containsId(new Identifier("registry_sync", "fabric_registry")));

		Validate.isTrue(RegistryAttributeHolder.get(fabricRegistry).hasAttribute(RegistryAttribute.MODDED));
		Validate.isTrue(RegistryAttributeHolder.get(fabricRegistry).hasAttribute(RegistryAttribute.SYNCED));
		Validate.isTrue(!RegistryAttributeHolder.get(fabricRegistry).hasAttribute(RegistryAttribute.PERSISTED));
	}
}
