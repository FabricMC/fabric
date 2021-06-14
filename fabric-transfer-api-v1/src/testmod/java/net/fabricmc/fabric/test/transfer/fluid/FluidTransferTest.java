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

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidTransfer;

public class FluidTransferTest implements ModInitializer {
	public static final String MOD_ID = "fabric-transfer-api-v1-testmod";

	private static final Block INFINITE_WATER_SOURCE = new Block(AbstractBlock.Settings.of(Material.METAL));
	private static final Block INFINITE_LAVA_SOURCE = new Block(AbstractBlock.Settings.of(Material.METAL));
	private static final Block FLUID_CHUTE = new FluidChuteBlock();
	public static BlockEntityType<FluidChuteBlockEntity> FLUID_CHUTE_TYPE;

	@Override
	public void onInitialize() {
		registerBlock(INFINITE_WATER_SOURCE, "infinite_water_source");
		registerBlock(INFINITE_LAVA_SOURCE, "infinite_lava_source");
		registerBlock(FLUID_CHUTE, "fluid_chute");

		FLUID_CHUTE_TYPE = FabricBlockEntityTypeBuilder.create(FluidChuteBlockEntity::new, FLUID_CHUTE).build();
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "fluid_chute"), FLUID_CHUTE_TYPE);

		FluidTransfer.SIDED.registerForBlocks((world, pos, state, be, direction) -> CreativeFluidStorage.WATER, INFINITE_WATER_SOURCE);
		FluidTransfer.SIDED.registerForBlocks((world, pos, state, be, direction) -> CreativeFluidStorage.LAVA, INFINITE_LAVA_SOURCE);
	}

	private static void registerBlock(Block block, String name) {
		Identifier id = new Identifier(MOD_ID, name);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
	}
}
