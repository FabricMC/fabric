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

package net.fabricmc.fabric.test.object.builder;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class BlockSettingsTest implements ModInitializer {
	public static final Block TEST_BLOCK_1 = new Block(FabricBlockSettings.of(Material.WOOD).pistonBehavior(PistonBehavior.DESTROY).replaceable(true).solid(false));
	public static final Block TEST_BLOCK_2 = new Block(FabricBlockSettings.of(Material.AIR).pistonBehavior(PistonBehavior.PUSH_ONLY).replaceable(false).solid(true));

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, ObjectBuilderTestConstants.id("test_block_1"), TEST_BLOCK_1);
		Registry.register(Registry.BLOCK, ObjectBuilderTestConstants.id("test_block_2"), TEST_BLOCK_2);
	}
}
