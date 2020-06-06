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

package net.fabricmc.fabric.test.transaction;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class TransactionTest implements ModInitializer {
	public static final String MODID = "fabric-transaction-api-v1-testmod";

	@Override
	public void onInitialize() {
		TransferBlock block = Registry.register(Registry.BLOCK, new Identifier(MODID, "transfer_block"), new TransferBlock(AbstractBlock.Settings.of(Material.STONE)));
		Registry.register(Registry.ITEM, new Identifier(MODID, "transfer_block"), new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
	}
}
