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

package net.fabricmc.fabric.test.resource.loader;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class VanillaBuiltinResourcePackInjectionTestMod implements ModInitializer {
	public static final String MODID = "fabric-resource-loader-v0-testmod";

	public static final Block TEST_BLOCK = new Block(AbstractBlock.Settings.copy(Blocks.STONE));

	@Override
	public void onInitialize() {
		Identifier id = new Identifier(MODID, "testblock");

		Registry.register(Registries.BLOCK, id, TEST_BLOCK);
		Registry.register(Registries.ITEM, id, new BlockItem(TEST_BLOCK, new Item.Settings()));
	}
}
