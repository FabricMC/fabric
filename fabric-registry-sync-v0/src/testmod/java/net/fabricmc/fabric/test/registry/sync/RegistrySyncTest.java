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
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

public class RegistrySyncTest implements ModInitializer {
	/**
	 * These are system property's as it allows for easier testing with different run configurations.
	 */
	public static final boolean REGISTER_BLOCKS = Boolean.parseBoolean(System.getProperty("fabric.registry.sync.test.register.blocks", "true"));
	public static final boolean REGISTER_ITEMS = Boolean.parseBoolean(System.getProperty("fabric.registry.sync.test.register.items", "true"));

	@Override
	public void onInitialize() {
		testBuiltInRegistrySync();

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

		Validate.isTrue(Registry.REGISTRIES.getIds().contains(new Identifier("registry_sync", "fabric_registry")));

		Validate.isTrue(RegistryAttributeHolder.get(fabricRegistry).hasAttribute(RegistryAttribute.MODDED));
		Validate.isTrue(RegistryAttributeHolder.get(fabricRegistry).hasAttribute(RegistryAttribute.SYNCED));
		Validate.isTrue(!RegistryAttributeHolder.get(fabricRegistry).hasAttribute(RegistryAttribute.PERSISTED));

		DynamicRegistrySetupCallback.EVENT.register(registryManager -> {
			RegistryEntryAddedCallback.event(registryManager.get(Registry.BIOME_KEY)).register((rawId, id, object) -> {
				System.out.println(id);
			});
		});
	}

	/**
	 * Tests that built-in registries are properly synchronized even after the dynamic reigstry managers have been
	 * class-loaded.
	 */
	private void testBuiltInRegistrySync() {
		System.out.println("Checking built-in registry sync...");

		// Register a configured feature before force-loading the dynamic registry manager
		ConfiguredFeature<DefaultFeatureConfig, ?> cf1 = Feature.BASALT_PILLAR.configure(DefaultFeatureConfig.INSTANCE);
		Identifier f1Id = new Identifier("registry_sync", "f1");
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, f1Id, cf1);

		// Force-Initialize the dynamic registry manager, doing this in a Mod initializer would cause
		// further registrations into BuiltInRegistries to _NOT_ propagate into DynamicRegistryManager.BUILTIN
		checkFeature(DynamicRegistryManager.create(), f1Id);

		ConfiguredFeature<DefaultFeatureConfig, ?> cf2 = Feature.DESERT_WELL.configure(DefaultFeatureConfig.INSTANCE);
		Identifier f2Id = new Identifier("registry_sync", "f2");
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, f2Id, cf2);

		DynamicRegistryManager.Impl impl2 = DynamicRegistryManager.create();
		checkFeature(impl2, f1Id);
		checkFeature(impl2, f2Id);
	}

	private void checkFeature(DynamicRegistryManager manager, Identifier id) {
		MutableRegistry<ConfiguredFeature<?, ?>> registry = manager.get(Registry.CONFIGURED_FEATURE_WORLDGEN);

		ConfiguredFeature<?, ?> builtInEntry = BuiltinRegistries.CONFIGURED_FEATURE.get(id);

		if (builtInEntry == null) {
			throw new IllegalStateException("Expected built-in entry to exist for: " + id);
		}

		ConfiguredFeature<?, ?> entry = registry.get(id);

		if (entry == null) {
			throw new IllegalStateException("Expected dynamic registry to contain entry " + id);
		}

		if (builtInEntry == entry) {
			throw new IllegalStateException("Expected that the built-in entry and dynamic entry don't have object identity because the dynamic entry is created by serializing the built-in entry to JSON and back.");
		}

		if (builtInEntry.feature != entry.feature) {
			throw new IllegalStateException("Expected both entries to reference the same feature since it's only in Registry and is never copied");
		}
	}
}
