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

package net.fabricmc.fabric.test.content.registry;

import net.minecraft.block.Blocks;
import net.minecraft.item.HoeItem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;

public final class ContentRegistryTest implements ModInitializer {
	@Override
	public void onInitialize() {
		// Expected behavior:
		//  - red wool is flattenable to yellow wool
		//  - quartz pillars are strippable to hay blocks
		//  - green wool is tillable to lime wool

		FlattenableBlockRegistry.register(Blocks.RED_WOOL, Blocks.YELLOW_WOOL.getDefaultState());
		StrippableBlockRegistry.register(Blocks.QUARTZ_PILLAR, Blocks.HAY_BLOCK);

		// assert that StrippableBlockRegistry throws when the blocks don't have 'axis'
		try {
			StrippableBlockRegistry.register(Blocks.BLUE_WOOL, Blocks.OAK_LOG);
			StrippableBlockRegistry.register(Blocks.HAY_BLOCK, Blocks.BLUE_WOOL);
			throw new AssertionError("StrippableBlockRegistry didn't throw when blocks where missing the 'axis' property!");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}

		TillableBlockRegistry.register(Blocks.GREEN_WOOL, context -> true, HoeItem.createTillAction(Blocks.LIME_WOOL.getDefaultState()));
	}
}
