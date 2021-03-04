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

package net.fabricmc.fabric.test.lookup;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.test.lookup.api.ItemApis;
import net.fabricmc.fabric.test.lookup.api.ItemInsertable;
import net.fabricmc.fabric.test.lookup.compat.InventoryExtractableProvider;
import net.fabricmc.fabric.test.lookup.compat.InventoryInsertableProvider;
import net.fabricmc.fabric.test.lookup.item.FabricItemApiLookupTest;

public class FabricApiLookupTest implements ModInitializer {
	public static final String MOD_ID = "fabric-lookup-api-v1-testmod";
	// Chute - Block without model that transfers item from the container above to the container below.
	// It's meant to work with unsided containers: chests, dispensers, droppers and hoppers.
	public static final ChuteBlock CHUTE_BLOCK = new ChuteBlock(FabricBlockSettings.of(Material.METAL));
	public static final BlockItem CHUTE_ITEM = new BlockItem(CHUTE_BLOCK, new Item.Settings().group(ItemGroup.MISC));
	public static BlockEntityType<ChuteBlockEntity> CHUTE_BLOCK_ENTITY_TYPE;
	// Cobble gen - Block without model that can generate infinite cobblestone when placed above a chute.
	// It's meant to test BlockApiLookup#registerSelf.
	public static final CobbleGenBlock COBBLE_GEN_BLOCK = new CobbleGenBlock(FabricBlockSettings.of(Material.METAL));
	public static final BlockItem COBBLE_GEN_ITEM = new BlockItem(COBBLE_GEN_BLOCK, new Item.Settings().group(ItemGroup.MISC));
	public static BlockEntityType<CobbleGenBlockEntity> COBBLE_GEN_BLOCK_ENTITY_TYPE;
	// Testing for item api lookups is done in the `item` package.

	@Override
	public void onInitialize() {
		Identifier chute = new Identifier(MOD_ID, "chute");
		Registry.register(Registry.BLOCK, chute, CHUTE_BLOCK);
		Registry.register(Registry.ITEM, chute, CHUTE_ITEM);
		CHUTE_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, chute, BlockEntityType.Builder.create(ChuteBlockEntity::new, CHUTE_BLOCK).build(null));

		Identifier cobbleGen = new Identifier(MOD_ID, "cobble_gen");
		Registry.register(Registry.BLOCK, cobbleGen, COBBLE_GEN_BLOCK);
		Registry.register(Registry.ITEM, cobbleGen, COBBLE_GEN_ITEM);
		COBBLE_GEN_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, cobbleGen, BlockEntityType.Builder.create(CobbleGenBlockEntity::new, COBBLE_GEN_BLOCK).build(null));

		InventoryExtractableProvider extractableProvider = new InventoryExtractableProvider();
		InventoryInsertableProvider insertableProvider = new InventoryInsertableProvider();

		ItemApis.INSERTABLE.registerForBlockEntities(insertableProvider, BlockEntityType.CHEST, BlockEntityType.DISPENSER, BlockEntityType.DROPPER, BlockEntityType.HOPPER);
		ItemApis.EXTRACTABLE.registerForBlockEntities(extractableProvider, BlockEntityType.CHEST, BlockEntityType.DISPENSER, BlockEntityType.DROPPER, BlockEntityType.HOPPER);
		ItemApis.EXTRACTABLE.registerSelf(COBBLE_GEN_BLOCK_ENTITY_TYPE);

		testLookupRegistry();
		testSelfRegistration();

		FabricItemApiLookupTest.onInitialize();
	}

	private static void testLookupRegistry() {
		BlockApiLookup<ItemInsertable, @NotNull Direction> insertable2 = BlockApiLookup.get(new Identifier("testmod:item_insertable"), ItemInsertable.class, Direction.class);

		if (insertable2 != ItemApis.INSERTABLE) {
			throw new AssertionError("The registry should have returned the same instance.");
		}

		ensureException(() -> {
			BlockApiLookup<Void, Void> wrongInsertable = BlockApiLookup.get(new Identifier("testmod:item_insertable"), Void.class, Void.class);
			wrongInsertable.registerFallback((world, pos, state, be, nocontext) -> null);
		}, "The registry should have prevented creation of another instance with different classes, but same id.");
	}

	private static void testSelfRegistration() {
		ensureException(() -> {
			ItemApis.INSERTABLE.registerSelf(COBBLE_GEN_BLOCK_ENTITY_TYPE);
		}, "The BlockApiLookup should have prevented self-registration of incompatible block entity types.");
	}

	public static void ensureException(Runnable runnable, String message) {
		boolean failed = false;

		try {
			runnable.run();
		} catch (Throwable t) {
			failed = true;
		}

		if (!failed) {
			throw new AssertionError(message);
		}
	}
}
