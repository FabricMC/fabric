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

package net.fabricmc.fabric.test.registry;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;

public final class ContentRegistryTest implements ModInitializer {
	@Override
	public void onInitialize() {
		CompostingChanceRegistry.INSTANCE.add(Items.OBSIDIAN, 0.5F);

		FlammableBlockRegistry.getDefaultInstance().add(Blocks.OBSIDIAN, 2, 3);
		FlammableBlockRegistry.getDefaultInstance().add(BlockTags.SNOW, 4, 6);

		FuelRegistry.INSTANCE.add(Items.OBSIDIAN, 20 * 3);
		FuelRegistry.INSTANCE.add(ItemTags.FOX_FOOD, 20 * 6);
	}
}
