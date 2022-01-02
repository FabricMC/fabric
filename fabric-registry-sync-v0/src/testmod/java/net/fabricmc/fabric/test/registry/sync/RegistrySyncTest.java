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

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.apache.commons.lang3.Validate;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
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
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler;
import net.fabricmc.fabric.impl.registry.sync.packet.NbtRegistryPacketHandler;
import net.fabricmc.fabric.impl.registry.sync.packet.RegistryPacketHandler;

public class RegistrySyncTest implements ModInitializer {
	/**
	 * These are system property's as it allows for easier testing with different run configurations.
	 */
	public static final boolean REGISTER_BLOCKS = Boolean.parseBoolean(System.getProperty("fabric.registry.sync.test.register.blocks", "true"));
	public static final boolean REGISTER_ITEMS = Boolean.parseBoolean(System.getProperty("fabric.registry.sync.test.register.items", "true"));

	public static final Identifier PACKET_CHECK_DIRECT = new Identifier("fabric-registry-sync-v0-v1-testmod:packet_check/direct");
	public static final RegistryPacketHandler DIRECT_PACKET_HANDLER = new DirectRegistryPacketHandler() {
		@Override
		public Identifier getPacketId() {
			return PACKET_CHECK_DIRECT;
		}
	};

	public static final Identifier PACKET_CHECK_NBT = new Identifier("fabric-registry-sync-v0-v1-testmod:packet_check/nbt");
	public static final RegistryPacketHandler NBT_PACKET_HANDLER = new NbtRegistryPacketHandler() {
		@Override
		public Identifier getPacketId() {
			return PACKET_CHECK_NBT;
		}
	};

	public static final Identifier PACKET_CHECK_COMPARE = new Identifier("fabric-registry-sync-v0-v1-testmod:packet_check/compare");

	@Override
	public void onInitialize() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Map<Identifier, Object2IntMap<Identifier>> map = RegistrySyncManager.createAndPopulateRegistryMap(true, null);
			NBT_PACKET_HANDLER.sendPacket(handler.player, map);
			DIRECT_PACKET_HANDLER.sendPacket(handler.player, map);
			sender.sendPacket(PACKET_CHECK_COMPARE, PacketByteBufs.empty());
		});

		testBuiltInRegistrySync();

		if (REGISTER_BLOCKS) {
			// For checking raw id bulk in direct registry packet, make registry_sync namespace have two bulks.
			registerBlocks("registry_sync", 5, 0);
			registerBlocks("registry_sync2", 50, 0);
			registerBlocks("registry_sync", 2, 5);

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

	private static void registerBlocks(String namespace, int amount, int startingId) {
		for (int i = 0; i < amount; i++) {
			Block block = new Block(AbstractBlock.Settings.of(Material.STONE));
			Registry.register(Registry.BLOCK, new Identifier(namespace, "block_" + (i + startingId)), block);

			if (REGISTER_ITEMS) {
				BlockItem blockItem = new BlockItem(block, new Item.Settings());
				Registry.register(Registry.ITEM, new Identifier(namespace, "block_" + (i + startingId)), blockItem);
			}
		}
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
		Registry<ConfiguredFeature<?, ?>> registry = manager.get(Registry.CONFIGURED_FEATURE_KEY);

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
