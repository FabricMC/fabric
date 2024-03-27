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

package net.fabricmc.fabric.test.datafixer.v1;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datafixer.v1.FabricDataFixerBuilder;
import net.fabricmc.fabric.api.datafixer.v1.FabricDataFixes;
import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class DataFixerTest implements ModInitializer, ServerLifecycleEvents.ServerStarted {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String MOD_ID = "fabric-data-fixer-api-v1-testmod";
	/**
	 * Note: must be equal to the one in {@code fabric.mod.json} (used for verifying).
	 */
	private static final int CURRENT_DATA_VERSION = 3;
	/**
	 * If {@code true}, generates a "base" save data for running a data fixer. If
	 * {@code false} (default), runs the data fixer.
	 */
	public static final boolean GENERATE_MODE = Boolean.getBoolean("fabric.dataFixerTestMod.genMode");

	public static final Identifier OLD_ITEM_ID = new Identifier(MOD_ID, "old_item");
	public static final Identifier NEW_ITEM_ID = new Identifier(MOD_ID, "new_item");
	public static final Item ITEM = new Item(new Item.Settings());

	public static final Identifier OLD_BLOCK_ID = new Identifier(MOD_ID, "old_block");
	public static final Identifier NEW_BLOCK_ID = new Identifier(MOD_ID, "new_block");
	public static final Block BLOCK = new Block(AbstractBlock.Settings.create());

	public static final Identifier OLD_BIOME_ID = new Identifier(MOD_ID, "old_biome");
	public static final Identifier NEW_BIOME_ID = new Identifier(MOD_ID, "new_biome");
	//public static final RegistryKey<Biome> BIOME_KEY = RegistryKey.of(
	//		RegistryKeys.BIOME, GENERATE_MODE ? OLD_BIOME_ID : NEW_BIOME_ID);
	//public static final Biome BIOME = TheEndBiomeCreator.createEndHighlands();

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, GENERATE_MODE ? OLD_ITEM_ID : NEW_ITEM_ID, ITEM);
		Registry.register(Registries.BLOCK, GENERATE_MODE ? OLD_BLOCK_ID : NEW_BLOCK_ID, BLOCK);
		//BuiltinRegistries.add(Registries.BIOME, BIOME_KEY, BIOME);
		//TheEndBiomes.addMainIslandBiome(BIOME_KEY, 10);

		ServerLifecycleEvents.SERVER_STARTED.register(this);

		if (!GENERATE_MODE) {
			// Not generate mode - run the data fixer and tests.
			initDataFixer();
			testNbt();
		}
	}

	private void initDataFixer() {
		ModContainer mod = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
		FabricDataFixerBuilder builder = FabricDataFixerBuilder.create(mod);

		if (builder.getDataVersion() != CURRENT_DATA_VERSION) {
			throw new AssertionError(String.format(Locale.ROOT, "Expected data version %d, got %d", CURRENT_DATA_VERSION, builder.getDataVersion()));
		}

		builder.addSchema(0, FabricDataFixes.getBaseSchema());

		Schema schema1 = builder.addSchema(1, IdentifierNormalizingSchema::new);
		SimpleFixes.addItemRenameFix(builder, "Rename old_item to new_item", OLD_ITEM_ID, NEW_ITEM_ID, schema1);

		Schema schema2 = builder.addSchema(2, IdentifierNormalizingSchema::new);
		SimpleFixes.addBlockRenameFix(builder, "Rename old_block to new_block", OLD_BLOCK_ID, NEW_BLOCK_ID, schema2);

		Schema schema3 = builder.addSchema(3, IdentifierNormalizingSchema::new);
		SimpleFixes.addBiomeRenameFix(builder, "Rename old_biome to new_biome", Map.of(OLD_BIOME_ID, NEW_BIOME_ID), schema3);

		FabricDataFixes.buildAndRegisterFixer(mod, builder);

		// Test that the data fixer exists
		FabricDataFixes.getFixers(MOD_ID).orElseThrow(() -> new AssertionError("Data fixer is not be registered"));
	}

	/**
	 * Tests parsing of various data version-containing NBTs.
	 */
	private void testNbt() {
		NbtCompound structureNbt = new NbtCompound();
		new StructureTemplate().writeNbt(structureNbt);
		int structureDataVersion = FabricDataFixes.getModDataVersion(structureNbt, MOD_ID);

		if (structureDataVersion != CURRENT_DATA_VERSION) {
			throw new AssertionError(
					String.format(Locale.ROOT, "Expected structure data version %d, got %d", CURRENT_DATA_VERSION, structureDataVersion)
			);
		}

		int unknownModDataVersion = FabricDataFixes.getModDataVersion(structureNbt, "tater");

		if (unknownModDataVersion != 0) {
			throw new AssertionError(
					String.format(Locale.ROOT, "Expected unknown mod data version %d, got %d", 0, unknownModDataVersion)
			);
		}

		int emptyNbtDataVersion = FabricDataFixes.getModDataVersion(new NbtCompound(), MOD_ID);

		if (emptyNbtDataVersion != 0) {
			throw new AssertionError(
					String.format(Locale.ROOT, "Expected empty NBT data version %d, got %d", 0, emptyNbtDataVersion)
			);
		}
	}

	@Override
	public void onServerStarted(MinecraftServer server) {
		ServerWorld world = server.getOverworld();
		ServerWorld end = Objects.requireNonNull(server.getWorld(World.END));
		LOGGER.info("Loading the End...");
		end.getChunk(0, 0); // Load chunks to generate modded biome/test upgrading

		// In non-generate mode, we only have to load the End, nothing else needed
		if (!GENERATE_MODE) return;

		LOGGER.info("Preparing world for data fixer testing...");

		// TODO: Use a custom chest to test the schema isolation issue.
		BlockPos chestPos = new BlockPos(0, 10, 0);
		world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());

		if (world.getBlockEntity(chestPos) instanceof ChestBlockEntity chestBlockEntity) {
			chestBlockEntity.setStack(0, ITEM.getDefaultStack());
		}

		world.setBlockState(chestPos.down(), BLOCK.getDefaultState());

		LOGGER.info("Generation finished, stopping server...");
		server.stop(false);
	}
}
