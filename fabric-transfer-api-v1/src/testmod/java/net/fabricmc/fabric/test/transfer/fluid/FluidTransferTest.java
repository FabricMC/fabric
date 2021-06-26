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

package net.fabricmc.fabric.test.transfer.fluid;

import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class FluidTransferTest implements ModInitializer {
	public static final String MOD_ID = "fabric-transfer-api-v1-testmod";

	private static final Block INFINITE_WATER_SOURCE = new Block(AbstractBlock.Settings.of(Material.METAL));
	private static final Block INFINITE_LAVA_SOURCE = new Block(AbstractBlock.Settings.of(Material.METAL));
	private static final Block FLUID_CHUTE = new FluidChuteBlock();
	private static final Item EXTRACT_STICK = new ExtractStickItem();
	public static BlockEntityType<FluidChuteBlockEntity> FLUID_CHUTE_TYPE;

	@Override
	public void onInitialize() {
		registerBlock(INFINITE_WATER_SOURCE, "infinite_water_source");
		registerBlock(INFINITE_LAVA_SOURCE, "infinite_lava_source");
		registerBlock(FLUID_CHUTE, "fluid_chute");
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "extract_stick"), EXTRACT_STICK);

		FLUID_CHUTE_TYPE = FabricBlockEntityTypeBuilder.create(FluidChuteBlockEntity::new, FLUID_CHUTE).build();
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "fluid_chute"), FLUID_CHUTE_TYPE);

		FluidStorage.SIDED.registerForBlocks((world, pos, state, be, direction) -> CreativeFluidStorage.WATER, INFINITE_WATER_SOURCE);
		FluidStorage.SIDED.registerForBlocks((world, pos, state, be, direction) -> CreativeFluidStorage.LAVA, INFINITE_LAVA_SOURCE);

		testFluidStorage();
	}

	private static void registerBlock(Block block, String name) {
		Identifier id = new Identifier(MOD_ID, name);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
	}

	private static void testFluidStorage() {
		SingleFluidStorage waterStorage = new SingleFluidStorage() {
			@Override
			protected long getCapacity(FluidKey fluidKey) {
				return BUCKET * 2;
			}

			@Override
			protected boolean canInsert(FluidKey fluidKey) {
				return fluidKey.isOf(Fluids.WATER);
			}
		};

		NbtCompound tag = new NbtCompound();
		tag.putInt("test", 1);
		FluidKey taggedWater = FluidKey.of(Fluids.WATER, tag);
		FluidKey taggedWater2 = FluidKey.of(Fluids.WATER, tag);
		FluidKey water = FluidKey.of(Fluids.WATER);
		FluidKey lava = FluidKey.of(Fluids.LAVA);

		// Test content
		if (!waterStorage.isEmpty()) throw new AssertionError("Should have been empty");

		// Test some insertions
		try (Transaction tx = Transaction.openOuter()) {
			// Should not allow lava (canInsert returns false)
			if (waterStorage.insert(lava, BUCKET, tx) != 0) throw new AssertionError("Lava inserted");
			// Should allow insert
			if (waterStorage.insert(taggedWater, BUCKET, tx) != BUCKET) throw new AssertionError("Tagged water insert 1 failed");
			// Keys are different, should not allow insert
			if (waterStorage.insert(water, BUCKET, tx) != 0) throw new AssertionError("Water inserted");
			// Should allow insert again even if the key is different cause they are equal
			if (waterStorage.insert(taggedWater2, BUCKET, tx) != BUCKET) throw new AssertionError("Tagged water insert 2 failed");
			// Should not allow further insertion because the storage is full
			if (waterStorage.insert(taggedWater, BUCKET, tx) != 0) throw new AssertionError("Storage full, yet something was inserted");
			// Should allow extraction
			if (waterStorage.extract(taggedWater2, BUCKET, tx) != BUCKET) throw new AssertionError("Extraction failed");
			// Re-insert
			if (waterStorage.insert(taggedWater2, BUCKET, tx) != BUCKET) throw new AssertionError("Tagged water insert 3 failed");
			// Test contents
			if (waterStorage.amount() != BUCKET * 2 || !waterStorage.resource().equals(taggedWater2)) throw new AssertionError("Contents are wrong");
			// No commit -> will abort
		}

		// Test content again to make sure the rollback worked as expected
		if (!waterStorage.isEmpty()) throw new AssertionError("Should have been empty");
	}
}
